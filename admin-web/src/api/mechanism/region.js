import request from '@/utils/request'

// 获取校区列表
export function getCampusList(data) {
  return request({
    url: 'base/campus/list',
    method: 'get',
    params: data
  })
}
// 新增校区 —— 社区版不支持（单机构单校区，指引 §4.2；后端 POST /base/campus 已移除）
// 获取校区详细信息
export function getCampusInfo(id) {
  return request({
    url: 'base/campus/' + id,
    method: 'get',
  })
}

// 修改校区信息
export function editCampusInfo(data) {
  return request({
    url: 'base/campus',
    method: 'put',
    data: data
  })
}

// 删除校区
export function delCampus(id) {
  return request({
    url: 'base/campus/' + id,
    method: 'delete',

  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: 'base/campus/resetPwd',
    method: 'delete',
    data: data
  })
}

// 进入校区
export function entryCampus(id) {
  return request({
    url: 'base/mechanism/into/' + id,
    method: 'post',
  })
}

// 获取经纬度 —— 社区版不含附近机构/课程等基于经纬度的平台能力（后端已移除）
