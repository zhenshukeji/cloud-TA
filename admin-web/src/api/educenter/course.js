import request from '@/utils/request'

// 查询课程列表（社区版只有常规课程，不含体验课程）
export function listCourse(query) {
  return request({
    url: '/educenter/course/list',
    method: 'get',
    params: query
  })
}

// 查询课程详细
export function getCourse(id) {
  return request({
    url: '/educenter/course/' + id,
    method: 'get'
  })
}
// 修改课程
export function updateCourse(data) {
  return request({
    url: '/educenter/course',
    method: 'put',
    data: data
  })
}

// 新增课程
export function addCourse(data) {
  return request({
    url: '/educenter/course',
    method: 'post',
    data: data
  })
}

// 删除课程
export function delCourse(id) {
  return request({
    url: '/educenter/course/' + id,
    method: 'delete'
  })
}

// 导出课程
export function exportCourse(query) {
  return request({
    url: '/educenter/course/export',
    method: 'get',
    params: query
  })
}
