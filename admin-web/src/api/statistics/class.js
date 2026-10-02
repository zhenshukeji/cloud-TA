import request from '@/utils/request'


// 获取班级列表
export function getClassList(query) {
  return request({
    url: '/statistics/class/list',
    method: 'get',
    params: query
  })
}


