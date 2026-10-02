import request from '@/utils/request'

// 登录方法
export function login(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/login',
    method: 'post',
    data: data
  })
}
// 机构人员选择校区后登录
export function campusLogin(data) {
  return request({
    url: 'base/access/login',
    method: 'get',
    params: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: '/captchaImage',
    method: 'get'
  })
}

// 社区版（OS011）：不提供短信验证码，因此没有「获取手机验证码 / 忘记密码两步」接口。
// 找回管理员口令由运维在部署环境重置；已登录用户改密走 /system/user/profile/updatePwd。
