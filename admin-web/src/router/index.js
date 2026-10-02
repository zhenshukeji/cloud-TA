import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                   // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true               // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect           // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'             // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * meta : {
    roles: ['admin','editor']    // 设置该路由进入的权限，支持多个权限叠加
    title: 'title'               // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'             // 设置该路由的图标，对应路径src/icons/svg
    breadcrumb: false            // 如果设置为false，则不会在breadcrumb面包屑中显示
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: (resolve) => require(['@/views/redirect'], resolve)
      }
    ]
  },
  {
    path: '/login',
    component: (resolve) => require(['@/views/login'], resolve),
    hidden: true
  },
  // 社区版（OS010）：不提供机构自助注册路由。单机构单校区，注册即「创建第二机构」，
  // 违反执行指引 §4.2「初始化完成后不开放创建第二机构和第二校区的入口/API」。

  {
    path: '/404',
    component: (resolve) => require(['@/views/error/404'], resolve),
    hidden: true
  },
  {
    path: '/401',
    component: (resolve) => require(['@/views/error/401'], resolve),
    hidden: true
  },
  {
    path: '/chooseCampus',
    component: (resolve) => require(['@/views/chooseCampus'], resolve),
    hidden: true
  },
  // {
  //   path: '',
  //   component: Layout,
  //   redirect: 'index',
  //   affix: false,
  //   children: [
  //     {
  //       path: 'index',
  //       component: (resolve) => require(['@/views/index'], resolve),
  //       name: '首页',
  //       meta: { title: '首页', icon: 'dashboard', noCache: true, affix: false }
  //     }
  //   ]
  // },
  // {
  //   path: '/mechanism',
  //   component: Layout,
  //   meta: { title: '机构端', icon: 'dashboard', noCache: true, affix: false },
  //   children: [
  //     {
  //       path: 'region',
  //       component: (resolve) => require(['@/views/mechanism/region'], resolve),
  //       name: '机构校区',
  //       meta: { title: '机构校区', icon: 'dashboard', noCache: true, affix: false }
  //     },
  //     {
  //       path: 'info',
  //       component: (resolve) => require(['@/views/mechanism/info'], resolve),
  //       name: '机构信息',
  //       meta: { title: '机构信息', icon: 'dashboard', noCache: true, affix: false }
  //     },
  //     {
  //       path: 'people',
  //       component: (resolve) => require(['@/views/mechanism/people'], resolve),
  //       name: '机构工作人员',
  //       meta: { title: '机构工作人员', icon: 'dashboard', noCache: true, affix: false }
  //     }
  //   ]
  // },
  // {
  //   path: '/curriculum',
  //   component: Layout,
  //   // redirect: 'curriculum',
  //   children: [
  //     {
  //       path: '',
  //       component: (resolve) => require(['@/views/curriculum'], resolve),
  //       name: '课表管理',
  //       meta: { title: '课表管理', icon: 'dashboard', noCache: true, affix: false }
  //     }
  //   ]
  // },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: (resolve) => require(['@/views/system/user/profile/index'], resolve),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },
  {
    path: '/dict',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'type/data/:dictId(\\d+)',
        component: (resolve) => require(['@/views/system/dict/data'], resolve),
        name: 'Data',
        meta: { title: '字典数据', icon: '' }
      }
    ]
  },
  // 社区版（OS010）：不提供「调度日志」与「代码生成器」的隐藏路由。
  // 指引 §3.2 要求生产监控入口与代码生成器不随社区版发布，其页面与 API 已一并删除。
]

export default new Router({
  //  mode: 'history', // 去掉url中的#
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
