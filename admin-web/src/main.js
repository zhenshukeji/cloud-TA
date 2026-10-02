import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
import permission from './directive/permission'

import './assets/icons' // icon
import './permission' // permission control
import { getDicts } from '@/api/system/dict/data'
import { getConfigKey } from '@/api/system/config'
import { parseTime, resetForm, addDateRange, selectDictLabel, download, handleTree, hasRole } from '@/utils/ruoyi'
import { sanitizeHtml } from '@/utils/sanitizeHtml'
import Pagination from '@/components/Pagination'
import moment from 'moment'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.hasRole = hasRole
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree
Vue.prototype.$moment = moment// 赋值使用
// 富文本净化（v-html 统一走此方法，防 XSS）
Vue.prototype.$sanitizeHtml = sanitizeHtml

Vue.prototype.msgSuccess = function(msg) {
  this.$message({ showClose: true, message: msg, type: 'success' })
}

Vue.prototype.msgError = function(msg) {
  this.$message({ showClose: true, message: msg, type: 'error', duration: 1500 })
}

Vue.prototype.msgInfo = function(msg) {
  this.$message.info(msg)
}

// 全局组件挂载
Vue.component('Pagination', Pagination)

Vue.use(permission)

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

// 全局错误捕获：避免组件渲染/运行时错误直接白屏且无任何反馈
Vue.config.errorHandler = function(err, vm, info) {
  console.error('[全局错误]', err, info)
  if (err && err.message) {
    // 仅在非生产环境给出轻量提示，避免打断用户操作
    if (process.env.NODE_ENV !== 'production') {
      console.warn('页面运行异常：' + err.message)
    }
  }
}

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
