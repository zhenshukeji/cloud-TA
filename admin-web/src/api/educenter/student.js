import request from '@/utils/request'

// 查询学生列表
export function listStudent(query) {
  return request({
    url: '/educenter/student/list',
    method: 'get',
    params: query
  })
}

//回收站学生
export function listRecycleStudent(query) {
  return request({
    url: '/educenter/student/list/recycle',
    method: 'get',
    params: query
  })
}

//恢复回收站学生
export function recoverStudent(id) {
  return request({
    url: '/educenter/student/recoverRec/' + id,
    method: 'get',
  })
}

// 查询学生详细
export function getStudent(data) {
  return request({
    url: '/educenter/student/base/' + data,
    method: 'get',
  })
}


// 修改学生
export function updateStudent(data) {
  return request({
    url: '/educenter/student',
    method: 'put',
    data: data
  })
}

// 新增学生
export function addStudent(data) {
  return request({
    url: '/educenter/student',
    method: 'post',
    data: data
  })
}


// 删除学生
export function delStudent(id) {
  return request({
    url: '/educenter/student/' + id,
    method: 'delete'
  })
}

// 获取账户信息（课次信息）
export function getInfoWithClassTime(query) {
  return request({
    url: '/educenter/student/getInfoWithClassTime',
    method: 'get',
    params: query
  })
}

// 修改课次记录
export function getInfoWithClsTimeUp(query) {
  return request({
    url: '/educenter/student/getInfoWithClsTimeUp',
    method: 'get',
    params: query
  })
}

// 课程购买记录
export function getInfoWithCouPur(query) {
  return request({
    url: '/educenter/student/getInfoWithCouPur',
    method: 'get',
    params: query
  })
}

// 课次更新
export function updateCourseTime(data) {
  return request({
    url: '/educenter/student/updateCourseTime',
    method: 'post',
    data
  })
}

// 查询课程列表
export function courseSelectAll() {
  return request({
    url: '/educenter/student/selectAllCourse',
    method: 'get',
  })
}

// 课程购买记录
export function purchase(data) {
  return request({
    url: '/educenter/student/addCoursePurchase',
    method: 'post',
    data: data
  })
}

// 修改总课次
export function opeTotalHour(data) {
  return request({
    url: '/educenter/student/updateTotalCourseTime',
    method: 'post',
    data
  })
}

// 导出学员课时账户（OS024，C14）：返回 AjaxResult，data 为生成文件名，随后用全局 download() 下载
export function exportStudentAccount(query) {
  return request({
    url: '/educenter/student/export',
    method: 'get',
    params: query
  })
}
