import request from '@/utils/request'

// 获取报表数据
export function getReportList(query) {
  return request({
    url: '/statistics/attendance/list',
    method: 'get',
    params: query
  })
}
