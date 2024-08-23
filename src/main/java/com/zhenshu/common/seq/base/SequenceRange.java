package com.zhenshu.common.seq.base;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceRange {

    private final long min;
    private final long max;
    private final AtomicLong value;
    private volatile boolean over = false;

    public SequenceRange(long min, long max) {
        this.min = min;
        this.max = max;
        this.value = new AtomicLong(min);
    }

    public long getAndIncrement() {
        long currentValue = this.value.getAndIncrement();
        if (currentValue > this.max) {
            this.over = true;
            return -1L;
        } else {
            return currentValue;
        }
    }

    public long getMin() {
        return this.min;
    }

    public long getMax() {
        return this.max;
    }

    public boolean isOver() {
        return this.over;
    }
}
