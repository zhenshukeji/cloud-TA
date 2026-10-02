import request from '@/utils/request'

// 获取机构校区工作人员列表
export function listMechanismPeople(data) {
  return request({
    url: 'base/mec_personnel/list',
    method: 'get',
    params: data
  })
}

// 新建机构工作人员
export function addMechanismPeople(data) {
  return request({
    url: 'base/mec_personnel',
    method: 'post',
    data: data
  })
}

// 修改机构工作人员
export function editMechanismPeople(data) {
  return request({
    url: 'base/mec_personnel',
    method: 'put',
    data: data
  })
}

// 获取机构工作人员详细信息
export function getPeopleInfo(id) {
  return request({
    url: 'base/mec_personnel/' + id,
    method: 'get'
  })
}

// 删除机构工作人员
export function delMechanismPeople(id) {
  return request({
    url: 'base/mec_personnel/' + id,
    method: 'delete',
  })
}

// 获取所属校区列表
export function getCampusList(data) {
  return request({
    url: 'base/campus/list',
    method: 'get',
    params: data
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: 'base/mec_personnel/resetPwd',
    method: 'delete',
    data: data
  })
}
