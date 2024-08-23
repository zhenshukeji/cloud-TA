package com.zhenshu.common.seq.dao;

import com.zhenshu.common.seq.base.SequenceRange;
import com.zhenshu.common.seq.exception.SequenceException;
import com.zhenshu.common.seq.util.SeqUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupSequenceDao extends AbstractSequenceDao {
    private static final Log log = LogFactory.getLog(GroupSequenceDao.class);
    private List<DataSource> dataSources;
    private int outStep;
    protected volatile String selectSql;
    protected volatile String updateSql;
    protected volatile String insertSql;
    private int dscount = 1;
    private ConcurrentHashMap<Integer, AtomicInteger> excludedKeyCount;
    private int maxSkipCount;
    private int protectMilliseconds;
    private ExecutorService exec;

    public GroupSequenceDao() {
        this.excludedKeyCount = new ConcurrentHashMap(this.dscount);
        this.maxSkipCount = 10;
        this.protectMilliseconds = 50;
        this.exec = Executors.newFixedThreadPool(1);
    }

    public void init() throws SequenceException, SQLException, PropertyVetoException {
        if (CollectionUtils.isEmpty(this.dataSources)) {
            SequenceException sequenceException = new SequenceException("dbGroupKeys is Null ");
            log.error("没有配置dbGroupKeys", sequenceException);
            throw sequenceException;
        } else {
            this.dscount = this.dataSources.size();
            this.outStep = this.getInnerStep() * this.dscount;
            this.insertSql = SeqUtil.getSeqInsertSql(this.getTableName(), this.getNameColumnName(), this.getValueColumnName(), this.getGmtCreateColumnName(), this.getGmtModifiedColumnName());
            this.selectSql = SeqUtil.getSeqSelectSql(this.getTableName(), this.getNameColumnName(), this.getValueColumnName());
            this.updateSql = SeqUtil.getSeqUpdateSql(this.getTableName(), this.getNameColumnName(), this.getValueColumnName(), this.getGmtModifiedColumnName());
        }
    }

    public SequenceRange nextRange(final String name) throws SequenceException {
        if (name == null) {
            log.error("序列名为空！");
            throw new IllegalArgumentException("序列名称不能为空");
        } else {
            int[] randomIntSequence = SeqUtil.randomIntSequence(this.dscount);

            for(int i = 0; i < this.getRetryTimes(); ++i) {
                for(int j = 0; j < this.dscount; ++j) {
                    boolean readSuccess = false;
                    boolean writeSuccess = false;
                    int index = randomIntSequence[j];
                    if (this.excludedKeyCount.get(index) != null) {
                        if (((AtomicInteger)this.excludedKeyCount.get(index)).incrementAndGet() <= this.maxSkipCount) {
                            continue;
                        }

                        this.excludedKeyCount.remove(index);
                        log.error(this.maxSkipCount + "次数已过，index为" + index + "的数据源后续重新尝试取序列");
                    }

                    final DataSource tGroupDataSource = (DataSource)this.dataSources.get(index);

                    long oldValue;
                    long newValue;
                    try {
                        if (this.excludedKeyCount.size() >= this.dscount - 1) {
                            oldValue = SeqUtil.selectSeqValue(tGroupDataSource, this.selectSql, name);
                        } else {
                            FutureTask future = new FutureTask(new Callable<Long>() {
                                public Long call() throws Exception {
                                    return SeqUtil.selectSeqValue(tGroupDataSource, GroupSequenceDao.this.selectSql, name);
                                }
                            });

                            try {
                                this.exec.submit(future);
                                oldValue = (Long)future.get((long)this.protectMilliseconds, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException var15) {
                                throw new SQLException("[SEQUENCE SLOW-PROTECTED MODE]:InterruptedException", var15);
                            } catch (ExecutionException var16) {
                                throw new SQLException("[SEQUENCE SLOW-PROTECTED MODE]:ExecutionException", var16);
                            } catch (TimeoutException var17) {
                                throw new SQLException("[SEQUENCE SLOW-PROTECTED MODE]:TimeoutException,当前设置超时时间为" + this.protectMilliseconds, var17);
                            }
                        }

                        StringBuilder message;
                        if (oldValue < 0L) {
                            message = new StringBuilder();
                            message.append("Sequence value cannot be less than zero, value = ").append(oldValue);
                            message.append(", please check table ").append(this.getTableName());
                            log.info(message);
                            continue;
                        }

                        if (oldValue > 9223372036754775807L) {
                            message = new StringBuilder();
                            message.append("Sequence value overflow, value = ").append(oldValue);
                            message.append(", please check table ").append(this.getTableName());
                            log.info(message);
                            continue;
                        }

                        newValue = oldValue + (long)this.outStep;
                        if (!this.check(index, newValue)) {
                            if (!this.isAdjust()) {
                                SequenceException sequenceException = new SequenceException(this.dataSources.get(index) + ":" + name + "的值得错误，覆盖到其他范围段了！请修改数据库，或者开启adjust开关！");
                                log.error(this.dataSources.get(index) + ":" + name + "的值得错误，覆盖到其他范围段了！请修改数据库，或者开启adjust开关！", sequenceException);
                                throw sequenceException;
                            }

                            newValue = newValue - newValue % (long)this.outStep + (long)this.outStep + (long)(index * this.getInnerStep());
                        }
                    } catch (SQLException var19) {
                        log.error("取范围过程中--查询出错！" + this.dataSources.get(index) + ":" + name, var19);
                        if (this.excludedKeyCount.size() < this.dscount - 1) {
                            this.excludedKeyCount.put(index, new AtomicInteger(0));
                            log.error("暂时踢除index为" + index + "的数据源，" + this.maxSkipCount + "次后重新尝试");
                        }
                        continue;
                    }

                    readSuccess = true;

                    try {
                        int affectedRows = SeqUtil.updateSeqValue(tGroupDataSource, this.updateSql, newValue, name, oldValue);
                        if (affectedRows == 0) {
                            continue;
                        }
                    } catch (Exception var18) {
                        log.error("取范围过程中--更新出错！" + this.dataSources.get(index) + ":" + name, var18);
                        continue;
                    }

                    writeSuccess = true;
                    if (readSuccess && writeSuccess) {
                        return new SequenceRange(newValue + 1L, newValue + (long)this.getInnerStep());
                    }
                }

                if (i == this.getRetryTimes() - 2) {
                    this.excludedKeyCount.clear();
                }
            }

            log.error("所有数据源都不可用！且重试" + this.getRetryTimes() + "次后，仍然失败!");
            throw new SequenceException("All dataSource faild to get value!");
        }
    }

    public void adjust(String name) throws SequenceException, SQLException {
        for(int i = 0; i < this.dataSources.size(); ++i) {
            DataSource tGroupDataSource = (DataSource)this.dataSources.get(i);
            if (tGroupDataSource == null) {
                log.debug("datasource is null:" + i);
            }

            try {
                Long value = SeqUtil.selectSeqValue(tGroupDataSource, this.selectSql, name);
                if (value == null) {
                    if (!this.isAdjust()) {
                        log.error("数据库中未配置该sequence！请往数据库中插入sequence记录，或者启动adjust开关");
                        throw new SequenceException("数据库中未配置该sequence！请往数据库中插入sequence记录，或者启动adjust开关");
                    }

                    this.adjustInsert(tGroupDataSource, i, name);
                } else if (!this.check(i, value)) {
                    if (!this.isAdjust()) {
                        log.error("数据库中配置的初值出错！请调整你的数据库，或者启动adjust开关");
                        throw new SequenceException("数据库中配置的初值出错！请调整你的数据库，或者启动adjust开关");
                    }

                    this.adjustUpdate(tGroupDataSource, i, value, name);
                }
            } catch (SQLException var5) {
                log.error("初值校验和自适应过程中出错.", var5);
                throw var5;
            }
        }

    }

    private void adjustUpdate(DataSource tGroupDataSource, int index, long value, String name) throws SequenceException, SQLException {
        long newValue = value - value % (long)this.outStep + (long)this.outStep + (long)(index * this.getInnerStep());

        try {
            int affectedRows = SeqUtil.updateSeqValue(tGroupDataSource, this.updateSql, newValue, name, value);
            if (affectedRows == 0) {
                throw new SequenceException("faild to auto adjust init value at  " + name + " update affectedRow =0");
            } else {
                log.info(this.dataSources.get(index) + "更新初值成功!" + "sequence Name：" + name + "更新过程：" + value + "-->" + newValue);
            }
        } catch (Exception var9) {
            log.error("由于SQLException,更新初值自适应失败！dbGroupIndex:" + this.dataSources.get(index) + "，sequence Name：" + name + "更新过程：" + value + "-->" + newValue, var9);
            throw new SequenceException("由于SQLException,更新初值自适应失败！dbGroupIndex:" + this.dataSources.get(index) + "，sequence Name：" + name + "更新过程：" + value + "-->" + newValue, var9);
        }
    }

    private void adjustInsert(DataSource tGroupDataSource, int index, String name) throws SequenceException, SQLException {
        long newValue = (long)(index * this.getInnerStep());

        try {
            int affectedRows = SeqUtil.insertSeq(tGroupDataSource, this.insertSql, name, newValue);
            if (affectedRows == 0) {
                throw new SequenceException("faild to auto adjust init value at  " + name + " update affectedRow =0");
            } else {
                log.info(this.dataSources.get(index) + "   name:" + name + "插入初值:" + name + "value:" + newValue);
            }
        } catch (Exception var7) {
            log.error("由于SQLException,插入初值自适应失败！dbGroupIndex:" + this.dataSources.get(index) + "，sequence Name：" + name + "   value:" + newValue, var7);
            throw new SequenceException("由于SQLException,插入初值自适应失败！dbGroupIndex:" + this.dataSources.get(index) + "，sequence Name：" + name + "   value:" + newValue, var7);
        }
    }

    private boolean check(int index, long value) {
        return value % (long)this.outStep == (long)(index * this.getInnerStep());
    }

    public List<DataSource> getDataSources() {
        return this.dataSources;
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
