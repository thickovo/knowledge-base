import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/documents'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/documents',
    name: 'DocumentList',
    component: () => import('@/views/DocumentList.vue'),
    meta: { requiresAuth: true, title: '文档列表' }
  },
  {
    path: '/document/create',
    name: 'DocumentCreate',
    component: () => import('@/views/DocumentEdit.vue'),
    meta: { requiresAuth: true, title: '新建文档' }
  },
  {
    path: '/document/edit/:id',
    name: 'DocumentEdit',
    component: () => import('@/views/DocumentEdit.vue'),
    meta: { requiresAuth: true, title: '编辑文档' }
  },
  {
    path: '/document/:id',
    name: 'DocumentDetail',
    component: () => import('@/views/DocumentDetail.vue'),
    meta: { requiresAuth: true, title: '文档详情' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/documents'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫:未登录跳登录页;已登录访问登录/注册页则跳主页
router.beforeEach((to, from, next) => {
  const token = getToken()
  const requiresAuth = to.meta.requiresAuth !== false

  if (requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/documents')
  } else {
    next()
  }
})

router.afterEach((to) => {
  if (to.meta && to.meta.title) {
    document.title = `${to.meta.title} - 知识库管理系统`
  }
})

export default router