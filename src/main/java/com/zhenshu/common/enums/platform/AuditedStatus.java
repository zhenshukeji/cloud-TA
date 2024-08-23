package com.zhenshu.common.enums.platform;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/23 17:10
 * @desc 机构审核状态
 */
public enum AuditedStatus {
    /**
     * 状态 0待提交 1待审核 2审核通过 3审核未通过
     */
    TO_BE_SUBMIT(0, "待提交"),
    TO_BE_AUDITED(1, "待审核"),
    AUDITED(2, "审核通过"),
    AUDIT_FAILED(3, "审核未通过");

    private final int code;
    private final String info;

    AuditedStatus(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }
}
