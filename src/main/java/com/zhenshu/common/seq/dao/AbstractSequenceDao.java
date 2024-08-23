package com.zhenshu.common.seq.dao;

import com.zhenshu.common.seq.base.SequenceDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractSequenceDao implements SequenceDao {
    private static final Log log = LogFactory.getLog(AbstractSequenceDao.class);
    protected static final int DEFAULT_INNER_STEP = 1000;
    protected static final int DEFAULT_RETRY_TIMES = 2;
    protected static final int MIN_STEP = 1;
    protected static final int MAX_STEP = 100000;
    protected static final String DEFAULT_TABLE_NAME = "sequence";
    protected static final String DEFAULT_NAME_COLUMN_NAME = "name";
    protected static final String DEFAULT_VALUE_COLUMN_NAME = "current_value";
    private static final String DEFAULT_GMT_MODIFIED_COLUMN_NAME = "gmt_modified";
    private static final String DEFAULT_GMT_CREATE_COLUMN_NAME = "gmt_create";
    protected static final Boolean DEFAULT_ADJUST = false;
    protected static final long DELTA = 100000000L;
    private int retryTimes = 2;
    private int innerStep = 1000;
    private boolean adjust;
    private String tableName;
    private String nameColumnName;
    private String valueColumnName;
    private String gmtModifiedColumnName;
    private String gmtCreateColumnName;
    private String appName;

    public AbstractSequenceDao() {
        this.adjust = DEFAULT_ADJUST;
        this.tableName = "sequence";
        this.nameColumnName = "name";
        this.valueColumnName = "current_value";
        this.gmtModifiedColumnName = "gmt_modified";
        this.gmtCreateColumnName = "gmt_create";
    }

    public int getRetryTimes() {
        return this.retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        if (retryTimes < 0) {
            log.warn("retryTimes is invalid " + retryTimes);
            throw new IllegalArgumentException("Property retryTimes cannot be less than zero, retryTimes = " + retryTimes);
        } else {
            this.retryTimes = retryTimes;
        }
    }

    public int getInnerStep() {
        return this.innerStep;
    }

    public void setInnerStep(int innerStep) {
        this.innerStep = innerStep;
        if (innerStep >= 1 && innerStep <= 100000) {
            this.innerStep = innerStep;
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Property step out of range [").append(1);
            message.append(",").append(100000).append("], step = ").append(innerStep);
            throw new IllegalArgumentException(message.toString());
        }
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getNameColumnName() {
        return this.nameColumnName;
    }

    public void setNameColumnName(String nameColumnName) {
        this.nameColumnName = nameColumnName;
    }

    public String getValueColumnName() {
        return this.valueColumnName;
    }

    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

    public String getGmtModifiedColumnName() {
        return this.gmtModifiedColumnName;
    }

    public void setGmtModifiedColumnName(String gmtModifiedColumnName) {
        this.gmtModifiedColumnName = gmtModifiedColumnName;
    }

    public String getGmtCreateColumnName() {
        return this.gmtCreateColumnName;
    }

    public void setGmtCreateColumnName(String gmtCreateColumnName) {
        this.gmtCreateColumnName = gmtCreateColumnName;
    }

    public boolean isAdjust() {
        return this.adjust;
    }

    public void setAdjust(boolean adjust) {
        this.adjust = adjust;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
