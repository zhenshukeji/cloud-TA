import request from '@/utils/request'

// 查询上课记录列表
export function listRecord(query) {
  return request({
    url: '/educenter/record/list',
    method: 'get',
    params: query
  })
}

// 查询上课记录详细信息
export function getRecordInfo(id) {
  return request({
    url: '/educenter/record/' + id,
    method: 'get'
  })
}

// 获取点名详细信息
export function getRollcallInfo(id) {
  return request({
    url: '/educenter/record/getCallRecordInfo/' + id,
    method: 'get'
  })
}

// 保存点名信息
export function saveRollcall(data) {
  return request({
    url: '/educenter/record/saveRollCall',
    method: 'put',
    data
  })
}

// 导出上课记录
export function exportRecord(query) {
  return request({
    url: '/educenter/record/export',
    method: 'get',
    params: query
  })
}

// 获取请假记录
export function getVacationList(query) {
  return request({
    url: '/educenter/vacate/list',
    method: 'get',
    params: query
  })
}

//获取请假详细信息
export function getVacationInfo(id) {
  return request({
    url: 'educenter/vacate/' + id,
    method: 'get'
  })
}

// 修改请假详细信息
export function editVacationInfo(data) {
  return request({
    url: 'educenter/vacate/',
    method: 'put',
    data
  })
}
