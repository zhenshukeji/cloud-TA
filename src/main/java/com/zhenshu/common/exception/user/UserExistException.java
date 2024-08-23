package com.zhenshu.common.exception.user;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/10 0010 14:15
 **/
public class UserExistException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserExistException() {
        super("user.exist", null);
    }
}
