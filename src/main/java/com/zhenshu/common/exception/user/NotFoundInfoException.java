package com.zhenshu.common.exception.user;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/5 0005 18:45
 **/
public class NotFoundInfoException extends UserException {
    public NotFoundInfoException() {
        super("user.info.not.exist" , null);
    }
}
