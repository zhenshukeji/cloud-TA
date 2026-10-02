import Cookies from 'js-cookie'

// 说明：前端通过 js-cookie 存储 token，浏览器端 Cookie 无法设置 httpOnly，
// 因此无法完全防御 XSS 读取。这里通过 secure + sameSite 尽量收敛风险，
// 根治方案需由后端下发 HttpOnly + Secure + SameSite 的 Cookie。
const TokenKey = 'jianxue-Admin-Token'
// 手动退出登录标记（会话级）：仅导航栏"退出登录"写入，命中时跳转登录页不携带 redirect 参数
const ManualLogoutKey = 'jianxue-manual-logout'

// 仅在 HTTPS 下才要求 secure，避免本地 http 调试时 Cookie 被丢弃
const cookieOptions = {
  secure: typeof location !== 'undefined' && location.protocol === 'https:',
  sameSite: 'Strict'
}

export function getToken() {
  return Cookies.get(TokenKey)
}

// 标记/取消 手动退出登录
export function setManualLogout(flag = true) {
  if (flag) {
    return sessionStorage.setItem(ManualLogoutKey, '1')
  }
  return sessionStorage.removeItem(ManualLogoutKey)
}

// 是否为手动退出登录（用于登录页忽略 redirect，直接进入对应首页）
export function isManualLogout() {
  return sessionStorage.getItem(ManualLogoutKey) === '1'
}

// 清除手动退出登录标记
export function clearManualLogout() {
  return sessionStorage.removeItem(ManualLogoutKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token, cookieOptions)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
