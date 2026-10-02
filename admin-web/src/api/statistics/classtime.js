import request from '@/utils/request'

// 获取课时数据
export function getTimeList(query) {
  return request({
    url: '/statistics/classTime/list',
    method: 'get',
    params: query
  })
}

// 获取课时详情
export function getTimeDetail(query) {
  return request({
    url: '/statistics/classTime/detail',
    method: 'get',
    params: query
  })
}