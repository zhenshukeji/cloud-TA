package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTemplateMessageBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTemplateMessageVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdTemplateMessageService;
import com.zhenshu.mapper.domain.educenter.BdTemplateMessage;
import com.zhenshu.mapper.educenter.BdTemplateMessageMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 模板消息发送记录 Service业务层处理
 *
 * @author jing
 * @date 2020-10-04
 */
@Service
public class BdTemplateMessageServiceImpl extends ServiceImpl<BdTemplateMessageMapper, BdTemplateMessage> implements IBdTemplateMessageService {

    /**
     * 查询模板消息发送记录
     *
     * @param id 模板消息发送记录 ID
     * @return 模板消息发送记录
     */
    @Override
    public BdTemplateMessageBO selectBdTemplateMessageById(Long id) {
        BdTemplateMessage param = new BdTemplateMessage();
        //未删除的 机构id，校区id
        param.setIsDelete(Constants.NOT_DELETED);
        param.setId(id);
        param.setMecId(SecurityUtils.getLoginCampusId());
        param.setCampusId(SecurityUtils.getLoginMecId());

        EntityWrapper<BdTemplateMessage> wrapper = new EntityWrapper<>();
        wrapper.setEntity(param);

        BdTemplateMessage result = this.selectOne(wrapper);
        BdTemplateMessageBO bdTemplateMessageBO = new BdTemplateMessageBO();
        BeanUtils.copyBeanProp(bdTemplateMessageBO, result);
        return bdTemplateMessageBO;
    }

    /**
     * 查询模板消息发送记录 列表
     *
     * @param bdTemplateMessageVO 模板消息发送记录
     * @return 模板消息发送记录
     */
    @Override
    public Page<BdTemplateMessageBO> selectBdTemplateMessageList(BdTemplateMessageVO bdTemplateMessageVO) {
        BdTemplateMessage bdTemplateMessage = new BdTemplateMessage();
        BeanUtils.copyBeanProp(bdTemplateMessage, bdTemplateMessageVO);
        EntityWrapper<BdTemplateMessage> wrapper = new EntityWrapper<>();
        //设置机构与校区id
        bdTemplateMessage.setIsDelete(Constants.NOT_DELETED);
        bdTemplateMessage.setMecId(SecurityUtils.getLoginMecId());
        bdTemplateMessage.setCampusId(SecurityUtils.getLoginCampusId());

        return getPage(bdTemplateMessage, bdTemplateMessageVO, wrapper);
    }

    /**
     * 无分页无参数列表
     */
    @Override
    public List<BdTemplateMessageBO> getAll(BdTemplateMessageVO bdTemplateMessageVO) {
        BdTemplateMessage bdTemplateMessage = new BdTemplateMessage();
        BeanUtils.copyBeanProp(bdTemplateMessage, bdTemplateMessageVO);
        EntityWrapper<BdTemplateMessage> wrapper = new EntityWrapper<>();
        //设置机构与校区id
        bdTemplateMessage.setIsDelete(Constants.NOT_DELETED);
        bdTemplateMessage.setMecId(SecurityUtils.getLoginMecId());
        bdTemplateMessage.setCampusId(SecurityUtils.getLoginCampusId());
        wrapper.setEntity(bdTemplateMessage);
        return copyList(this.selectList(wrapper));
    }

    /**
     * 新增模板消息发送记录
     *
     * @param bdTemplateMessageVO 模板消息发送记录
     * @return 结果
     */
    @Override
    public boolean insertBdTemplateMessage(BdTemplateMessageVO bdTemplateMessageVO) {
        BdTemplateMessage bdTemplateMessage = new BdTemplateMessage();
        BeanUtils.copyBeanProp(bdTemplateMessage, bdTemplateMessageVO);
        //设置机构id与校区id
        bdTemplateMessage.setMecId(SecurityUtils.getLoginMecId());
        bdTemplateMessage.setCampusId(SecurityUtils.getLoginCampusId());
        bdTemplateMessage.setCreateTime(DateUtils.getNowDate());
        bdTemplateMessage.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdTemplateMessage);
    }

    /**
     * 修改模板消息发送记录
     *
     * @param bdTemplateMessageVO 模板消息发送记录
     * @return 结果
     */
    @Override
    public boolean updateBdTemplateMessage(BdTemplateMessageVO bdTemplateMessageVO) {
        BdTemplateMessage bdTemplateMessage = new BdTemplateMessage();
        BeanUtils.copyBeanProp(bdTemplateMessage, bdTemplateMessageVO);
        bdTemplateMessage.setUpdateTime(DateUtils.getNowDate());
        EntityWrapper<BdTemplateMessage> wrapper = new EntityWrapper<>();
        wrapper.eq("id", bdTemplateMessage.getId());
        return this.update(bdTemplateMessage, wrapper);
    }

    /**
     * 批量删除模板消息发送记录
     *
     * @param idList 需要删除的模板消息发送记录 ID
     * @return 结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean deleteBdTemplateMessageByIds(Collection<? extends Serializable> idList) {
        List<Long> list = (List<Long>) idList;
        boolean flag = true;
        for (Long id : list) {
            flag = deleteBdTemplateMessageById(id);
            if (!flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * 删除模板消息发送记录 信息
     *
     * @param id 模板消息发送记录 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdTemplateMessageById(Long id) {
        BdTemplateMessage bdTemplateMessage = new BdTemplateMessage();
        bdTemplateMessage.setIsDelete(Constants.DELETED);
        bdTemplateMessage.setId(id);
        return this.updateById(bdTemplateMessage);
    }

    /**
     * 进行分页
     *
     * @param bdTemplateMessage   数据库对象 设置查询参数
     * @param bdTemplateMessageVO 入参对象
     * @param wrapper             查询参数
     * @return Page<BdTemplateMessageBO>分页好的出参
     */
    public Page<BdTemplateMessageBO> getPage(BdTemplateMessage bdTemplateMessage, BdTemplateMessageVO bdTemplateMessageVO, EntityWrapper<BdTemplateMessage> wrapper) {
        wrapper.setEntity(bdTemplateMessage);
        Page<BdTemplateMessage> page = this.selectPage(new Page<>(bdTemplateMessageVO.getPageNum(), bdTemplateMessageVO.getPageSize()), wrapper);
        List<BdTemplateMessageBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdTemplateMessageBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdTemplateMessages 数据库对象集合
     * @return List<BdTemplateMessageBO> bo对象集合
     */
    public List<BdTemplateMessageBO> copyList(List<BdTemplateMessage> bdTemplateMessages) {
        List<BdTemplateMessageBO> resultList = new ArrayList<>();
        for (BdTemplateMessage source : bdTemplateMessages) {
            BdTemplateMessageBO BdTemplateMessageBO = new BdTemplateMessageBO();
            BeanUtils.copyBeanProp(BdTemplateMessageBO, source);
            resultList.add(BdTemplateMessageBO);
        }
        return resultList;
    }
}
