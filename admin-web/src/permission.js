import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken, isManualLogout } from '@/utils/auth'

NProgress.configure({ showSpinner: false });

const whiteList = ['/login'];

router.beforeEach((to, from, next) => {
  NProgress.start();
  if (getToken()) {
    /* has token*/
    if (to.path === '/login') {
      next()
      // next({ path: '/' });
      NProgress.done()
    } else {
      if (store.getters.roles.length === 0) {
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(res => {
          // 拉取user_info
          const roles = res.roles;
          const { userType } = res.user
          // 机构人员（userType=1）需要先选择校区
          if (userType === '1' && !res.user.campusId) {
            next({ path: '/chooseCampus' })
            return false
          }
          store.dispatch('GenerateRoutes', { roles }).then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes); // 动态添加可访问路由表
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        })
          .catch(err => {
            console.log('拉取失败')
            store.dispatch('FedLogOut').then(() => {
              Message.error(err);
            })
          })
      } else {
        console.log('判断走这')
        next()
        // 没有动态改变权限的需求可直接next() 删除下方权限判断 ↓
        // if (hasPermission(store.getters.roles, to.meta.roles)) {
        //   next()
        // } else {
        //   next({ path: '/401', replace: true, query: { noGoBack: true }})
        // }
        // 可删 ↑
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else if (isManualLogout()) {
      // 手动退出登录：不记录 redirect 地址，避免切换账号后跳到无权限的页面，登录后进入对应首页
      next({ path: '/login', replace: true })
      NProgress.done()
    } else {
      next(`/login?redirect=${to.path}`); // 否则全部重定向到登录页
      NProgress.done()
    }
  }
});

router.afterEach(() => {
  NProgress.done()
});
