package com.zhenshu.common.seq.base;

import com.zhenshu.common.seq.exception.SequenceException;

import java.sql.SQLException;

public interface SequenceDao {
    SequenceRange nextRange(String var1) throws SequenceException;

    void adjust(String var1) throws SequenceException, SQLException;
}
