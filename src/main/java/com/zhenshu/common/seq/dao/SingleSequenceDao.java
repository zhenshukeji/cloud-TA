package com.zhenshu.common.seq.dao;

import com.zhenshu.common.seq.base.SequenceRange;
import com.zhenshu.common.seq.exception.SequenceException;
import com.zhenshu.common.seq.util.SeqUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class SingleSequenceDao extends AbstractSequenceDao {
    private static final Log log = LogFactory.getLog(SingleSequenceDao.class);
    private DataSource dataSource;
    protected volatile String selectSql;
    protected volatile String updateSql;
    protected volatile String insertSql;

    public SingleSequenceDao() {
    }

    public void init() throws SequenceException {
        if (this.getDataSource() == null) {
            SequenceException sequenceException = new SequenceException("datasource is Null ");
            log.error("没有配置datasource", sequenceException);
            throw sequenceException;
        } else {
            this.insertSql = SeqUtil.getSeqInsertSql(this.getTableName(), this.getNameColumnName(), this.getValueColumnName(), this.getGmtCreateColumnName(), this.getGmtModifiedColumnName());
            this.selectSql = SeqUtil.getSeqSelectSql(this.getTableName(), this.getNameColumnName(), this.getValueColumnName());
            this.updateSql = SeqUtil.getSeqUpdateSql(this.getTableName(), this.getNameColumnName(), this.getValueColumnName(), this.getGmtModifiedColumnName());
        }
    }

    public SequenceRange nextRange(String name) throws SequenceException {
        if (name == null) {
            throw new IllegalArgumentException("序列名称不能为空");
        } else {
            for(int i = 0; i < this.getRetryTimes() + 1; ++i) {
                Long oldValue = SeqUtil.selectSeqValue(this.getDataSource(), this.selectSql, name);
                if (oldValue == null) {
                    try {
                        this.adjust(name);
                    } catch (SQLException var6) {
                        throw new SequenceException(var6.getMessage(), var6);
                    }
                }

                StringBuilder message;
                if (oldValue < 0L) {
                    message = new StringBuilder();
                    message.append("Sequence value cannot be less than zero, value = ").append(oldValue);
                    message.append(", please check table ").append(this.getTableName());
                    throw new SequenceException(message.toString());
                }

                if (oldValue > 9223372036754775807L) {
                    message = new StringBuilder();
                    message.append("Sequence value overflow, value = ").append(oldValue);
                    message.append(", please check table ").append(this.getTableName());
                    throw new SequenceException(message.toString());
                }

                Long newValue = oldValue + (long)this.getInnerStep();
                int affectedRows = SeqUtil.updateSeqValue(this.getDataSource(), this.updateSql, newValue, name, oldValue);
                if (affectedRows != 0) {
                    return new SequenceRange(oldValue + 1L, newValue);
                }
            }

            throw new SequenceException("Retried too many times, retryTimes = " + this.getRetryTimes());
        }
    }

    public void adjust(String name) throws SequenceException, SQLException {
        try {
            Long value = SeqUtil.selectSeqValue(this.getDataSource(), this.selectSql, name);
            if (value == null) {
                if (!this.isAdjust()) {
                    log.error("数据库中未配置该sequence！请往数据库中插入sequence记录，或者启动adjust开关,seq name:" + name);
                    throw new SequenceException("数据库中未配置该sequence！请往数据库中插入sequence记录，或者启动adjust开关,seq name:" + name);
                }

                this.adjustInsert(name);
            }

        } catch (SQLException var3) {
            log.error("初值校验和自适应过程中出错.", var3);
            throw var3;
        }
    }

    private void adjustInsert(String name) throws SequenceException, SQLException {
        int newValue = this.getInnerStep();
        SeqUtil.insertSeq(this.getDataSource(), this.insertSql, name, (long)newValue);
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
