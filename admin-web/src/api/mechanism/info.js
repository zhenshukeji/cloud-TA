import request from '@/utils/request'
// 获取机构信息
export function getMechanismInfo() {
  return request({
    url: 'base/mechanism/get',
    method: 'get'
  })
}
// 修改机构信息
export function updateMechanismInfo(data) {
  return request({
    url: '/base/mechanism/updateBdMechanism',
    method: 'put',
    data: data
  })
}
// 社区版（OS011）：换绑机构负责人手机号原依赖短信验证码，已随短信链路一并移除。
// 需要修改负责人手机号时，请在「系统管理 - 用户管理」中直接编辑该账号的手机号。
//修改机构logo 和名称
export function updateInfo(data) {
  return request({
    url: '/base/mechanism/updateNameOrLogo',
    method: 'put',
    data
  })
}

