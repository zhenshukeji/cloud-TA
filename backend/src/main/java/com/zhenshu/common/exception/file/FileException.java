package com.zhenshu.common.exception.file;

import com.zhenshu.common.exception.BaseException;

/**
 * 文件信息异常类
 *
 * @author zhenshu
 */
public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

    public FileException(String message) {
        super(message);
    }
}
