import request from '@/utils/request'

// 查询课次 列表
export function selectAll(query) {
    return request({
        url: '/educenter/curriculum/selectAll',
        method: 'get',
        params: query
    })
}

// 通过班级上课进入点名页面
export function intoRollCall(query) {
    return request({
        url: '/educenter/curriculum/intoRollCall',
        method: 'get',
        params: query
    })
}

// 点名
export function rollCall(query) {
    return request({
        url: '/educenter/curriculum/rollCall',
        method: 'post',
        data: query
    })
}

// 编辑课表信息中的课次
export function changeClassTime(query) {
    return request({
        url: '/educenter/curriculum/changeClassTime',
        method: 'put',
        data: query
    })
}

// 删除课表信息中的课次
export function deleteClassTime(query) {
    return request({
        url: '/educenter/curriculum/deleteClassTime',
        method: 'put',
        data: query
    })
}

// 获取班级上课次间详细信息
export function getInfo(query) {
    return request({
        url: '/educenter/curriculum/getInfo',
        method: 'get',
        params: query
    })
}

// 新增课表
export function insertChangeClass(query) {
    return request({
        url: '/educenter/curriculum/insertChangeClass',
        method: 'post',
        data: query
    })
}

// 获取班级
export function selectAllClass(query) {
    return request({
        url: '/educenter/curriculum/selectAllClass',
        method: 'get',
        params: query
    })
}