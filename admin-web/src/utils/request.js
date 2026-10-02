import axios from 'axios'
import { Notification, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'

// 防止 401 处理过程中出现递归刷新（过期 token 导致 /logout 也返回 401 时）
let isHandling401 = false

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: process.env.VUE_APP_BASE_API,
  // 超时
  timeout: 50000
})
// request拦截器
service.interceptors.request.use(config => {
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  return config
}, error => {
  console.error(error)
  return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
  // 未设置状态码则默认成功状态
  const code = res.data.code || 200
  // 获取错误信息
  const message = errorCode[code] || res.data.msg || errorCode['default']
  if (code === 401) {
    if (!isHandling401) {
      isHandling401 = true
      store.dispatch('LogOut').then(() => {
        location.reload() // 为了重新实例化vue-router对象 避免bugx
      }).catch(() => {
        location.reload()
      })
    }
    return Promise.reject(new Error(message))
    // MessageBox.confirm(
    //   '登录状态已过期，您可以继续留在该页面，或者重新登录',
    //   '系统提示',
    //   {
    //     confirmButtonText: '重新登录',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   }
    // ).then(() => {
    //   store.dispatch('LogOut').then(() => {
    //     location.reload() // 为了重新实例化vue-router对象 避免bugx
    //   })
    // })
  } else if (code === 400500) {
    return Promise.reject(new Error(message))
  } else if (code === 500) {
    Message({
      message: message,
      type: 'error'
    })
    return Promise.reject(new Error(message))
  } else if (code === 400200) {
    Message({
      message: message,
      type: 'error'
    })
    return Promise.reject(new Error(message))
  } else if (code === 400600) {
    // 获取定位接口
    return Promise.reject(new Error(message))
  } else if (code !== 200 && code !== 300036) {
    Notification.error({
      title: message
    })
    return Promise.reject('error')
  } else {
    return res.data
  }
},
error => {
  console.log('err' + error)
  // 优先取后端返回的具体错误信息，其次取超时/网络错误提示
  let message = error.message
  if (error.response && error.response.data && error.response.data.msg) {
    message = error.response.data.msg
  } else if (error.code === 'ECONNABORTED' || (error.message && error.message.indexOf('timeout') > -1)) {
    message = '请求超时，请重试'
  }
  Message({
    message: message,
    type: 'error',
    duration: 5 * 1000
  })
  return Promise.reject(error)
}
)

export default service
