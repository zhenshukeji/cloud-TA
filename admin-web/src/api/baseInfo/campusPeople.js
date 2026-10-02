import request from '@/utils/request'

// 查询校区人员列表
export function listCampus_personnel(query) {
  return request({
    url: '/base/campus_personnel/list',
    method: 'get',
    params: query
  })
}

// 查询校区人员详细
export function getCampus_personnel(id) {
  return request({
    url: '/base/campus_personnel/' + id,
    method: 'get'
  })
}
// 修改校区人员
export function updateCampus_personnel(data) {
  return request({
    url: '/base/campus_personnel',
    method: 'put',
    data: data
  })
}

// 新增校区人员
export function addCampus_personnel(data) {
  return request({
    url: '/base/campus_personnel',
    method: 'post',
    data: data
  })
}

// 删除校区人员
export function delCampus_personnel(id) {
  return request({
    url: '/base/campus_personnel/' + id,
    method: 'delete'
  })
}

// 导出校区人员
export function exportCampus_personnel(query) {
  return request({
    url: '/base/campus_personnel/export',
    method: 'get',
    params: query
  })
}

// 获取校区角色列表
export function getRoleList() {
  return request({
    url: '/base/campus_personnel/allRole',
    method: 'post'
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: 'base/campus_personnel/resetPwd',
    method: 'DELETE',
    data
  })
}
