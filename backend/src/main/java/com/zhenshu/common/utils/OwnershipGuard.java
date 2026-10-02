package com.zhenshu.common.utils;

import com.zhenshu.common.exception.CustomException;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;

import java.util.Objects;

/**
 * 数据归属守卫（OS023）：写操作的目标对象必须属于当前会话的机构（进了校区则还须同校区）。
 *
 * <p>服务端数据归属是 §7.3 强制口径：客户端伪造归属字段不改变落库归属（由各 Manager 统一取会话归属实现），
 * 而对象 ID 指向其他机构/校区的诱饵数据时必须在此被拒——不允许靠「列表里看不见」代替服务端校验。
 *
 * <p>口径：会话 campusId 为 null（机构管理员未进校区）时只校验机构归属；进了校区后机构+校区都必须一致。
 */
public final class OwnershipGuard {

    private OwnershipGuard() {
    }

    /** 校验目标对象的归属；不匹配即抛出业务异常（令事务回滚）。 */
    public static void checkOwned(Long mecId, Long campusId) {
        SysUser user = SecurityUtils.getSysUser();
        if (user == null) {
            throw new CustomException("未获取到登录用户，拒绝操作");
        }
        if (!Objects.equals(user.getMecId(), mecId)) {
            throw new CustomException("无权操作其他机构的数据");
        }
        if (user.getCampusId() != null && !Objects.equals(user.getCampusId(), campusId)) {
            throw new CustomException("无权操作其他校区的数据");
        }
    }
}
