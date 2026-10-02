package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTemplateMessageBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTemplateMessageVO;
import com.zhenshu.mapper.domain.educenter.BdTemplateMessage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 模板消息发送记录 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdTemplateMessageService extends IService<BdTemplateMessage> {

    /**
     * 查询模板消息发送记录
     *
     * @param id 模板消息发送记录 ID
     * @return 模板消息发送记录
     */
    BdTemplateMessageBO selectBdTemplateMessageById(Long id);

    /**
     * 分页查询模板消息发送记录 列表
     *
     * @param bdTemplateMessageVO 模板消息发送记录
     * @return 分页模板消息发送记录
     */
    Page<BdTemplateMessageBO> selectBdTemplateMessageList(BdTemplateMessageVO bdTemplateMessageVO);

    /**
     * 无分页无参数模板消息发送记录 列表
     *
     * @return 模板消息发送记录 集合
     */
    List<BdTemplateMessageBO> getAll(BdTemplateMessageVO bdTemplateMessageVO);

    /**
     * 新增模板消息发送记录
     *
     * @param bdTemplateMessageVO 模板消息发送记录
     * @return 结果
     */
    boolean insertBdTemplateMessage(BdTemplateMessageVO bdTemplateMessageVO);

    /**
     * 修改模板消息发送记录
     *
     * @param bdTemplateMessageVO 模板消息发送记录
     * @return 结果
     */
    boolean updateBdTemplateMessage(BdTemplateMessageVO bdTemplateMessageVO);

    /**
     * 批量删除模板消息发送记录
     *
     * @param idList 需要删除的模板消息发送记录 ID
     * @return 结果
     */
    boolean deleteBdTemplateMessageByIds(Collection<? extends Serializable> idList);

    /**
     * 删除模板消息发送记录 信息
     *
     * @param id 模板消息发送记录 ID
     * @return 结果
     */
    boolean deleteBdTemplateMessageById(Long id);
}
