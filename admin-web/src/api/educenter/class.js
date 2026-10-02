import request from '@/utils/request'
// 查询普通班级列表（社区版只有常规班级；体验班级链路已按指引 §3.2 移除）
export function listClass(query) {
  return request({
    url: '/educenter/class/list',
    method: 'get',
    params: query
  })
}

// 查询班级详细
export function getClass(id) {
  return request({
    url: '/educenter/class/' + id,
    method: 'get'
  })
}
// 修改班级
export function updateClass(data) {
  return request({
    url: '/educenter/class',
    method: 'put',
    data: data
  })
}

// 新增班级
export function addClass(data) {
  return request({
    url: '/educenter/class',
    method: 'post',
    data: data
  })
}

// 删除班级
export function delClass(id) {
  return request({
    url: '/educenter/class/' + id,
    method: 'delete'
  })
}

// 导出班级
export function exportClass(query) {
  return request({
    url: '/educenter/class/export',
    method: 'get',
    params: query
  })
}

// 获取课程列表
export function getCourseList(query) {
  return request({
    url: '/educenter/class/selectAllCourse',
    method: 'get',
    params: query
  })
}

//获取老师列表
export function getTeacherList(query) {
  return request({
    url: '/educenter/class/selectAllTeacher',
    method: 'get',
    params: query
  })
}

// 获取该班级学员列表
export function getStudentList(query) {
  return request({
    url: '/educenter/class/getStudentsByCls',
    method: 'get',
    params: query
  })
}

//获取需要可新增的学员列表
export function getAddStudentList(query) {
  return request({
    url: '/educenter/class/getStudents',
    method: 'get',
    params: query
  })
}

// 新增学生
export function AddStudent(data) {
  return request({
    url: '/educenter/class/addStuClass',
    method: 'post',
    data
  })
}

// 移出学生
export function removeStudent(data) {
  return request({
    url: '/educenter/class/classRemoveStu',
    method: 'post',
    data
  })
}

// 获取其他班级列表
export function getExtraClass(data) {
  return request({
    url: '/educenter/class/selectOtherClass',
    method: 'post',
    data
  })
}

// 导入
export function exportStudent(data) {
  return request({
    url: '/educenter/class/importStuToClass',
    method: 'post',
    data
  })
}
