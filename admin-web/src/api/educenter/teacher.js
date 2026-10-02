import request from '@/utils/request'

// 查询老师 老师列表
export function listTeacher(query) {
  return request({
    url: '/educenter/teacher/list',
    method: 'get',
    params: query
  })
}

// 查询老师详细
export function getTeacher(id) {
  return request({
    url: '/educenter/teacher/' + id,
    method: 'get'
  })
}
// 修改老师 
export function updateTeacher(data) {
  return request({
    url: '/educenter/teacher',
    method: 'put',
    data: data
  })
}

// 新增老师 
export function addTeacher(data) {
  return request({
    url: '/educenter/teacher',
    method: 'post',
    data: data
  })
}

// 删除老师 
export function delTeacher(id) {
  return request({
    url: '/educenter/teacher/' + id,
    method: 'delete'
  })
}

// 导出老师
export function exportTeacher(query) {
  return request({
    url: '/educenter/teacher/export',
    method: 'get',
    params: query
  })
}

// 获取校区人员
export function getCampusPerson(query) {
  return request({
    url: '/educenter/teacher/getCampusPerson',
    method: 'get',
    params: query
  })
}

// 获取机构人员
export function getMechanismPerson(query) {
  return request({
    url: '/educenter/teacher/getMecPerson',
    method: 'get',
    params: query
  })
}
