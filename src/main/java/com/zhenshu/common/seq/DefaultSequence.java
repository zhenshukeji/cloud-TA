package com.zhenshu.common.seq;

import com.zhenshu.common.seq.base.Sequence;
import com.zhenshu.common.seq.base.SequenceDao;
import com.zhenshu.common.seq.base.SequenceRange;
import com.zhenshu.common.seq.exception.SequenceException;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultSequence implements Sequence {
    private final Lock lock = new ReentrantLock();
    private SequenceDao sequenceDao;
    private String name;
    private volatile SequenceRange currentRange;

    public DefaultSequence() {
    }

    public void init() throws SequenceException, SQLException {
        synchronized(this) {
            this.sequenceDao.adjust(this.name);
        }
    }

    public long nextValue() {
        if (this.currentRange == null) {
            this.lock.lock();
            try {
                if (this.currentRange == null) {
                    this.currentRange = this.sequenceDao.nextRange(this.name);
                }
            } finally {
                this.lock.unlock();
            }
        }
        long value = this.currentRange.getAndIncrement();
        if (value == -1L) {
            this.lock.lock();
            try {
                do {
                    if (this.currentRange.isOver()) {
                        this.currentRange = this.sequenceDao.nextRange(this.name);
                    }
                    value = this.currentRange.getAndIncrement();
                } while(value == -1L);
            } finally {
                this.lock.unlock();
            }
        }
        if (value < 0L) {
            throw new SequenceException("Sequence value overflow, value = " + value);
        } else {
            return value;
        }
    }

    public SequenceDao getSequenceDao() {
        return this.sequenceDao;
    }

    public void setSequenceDao(SequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
