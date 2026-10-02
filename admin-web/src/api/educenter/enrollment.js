import request from '@/utils/request'

// 查询报读情况
export function listEnrollment(query) {
  return request({
    url: '/educenter/enrollment/list',
    method: 'get',
    params: query
  })
}
