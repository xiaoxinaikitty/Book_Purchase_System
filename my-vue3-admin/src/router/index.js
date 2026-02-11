import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  // 用户端路由
  {
    path: '/login',
    name: 'UserLogin',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '用户登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'UserRegister',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '用户注册', requiresAuth: false }
  },
  {
    path: '/home',
    name: 'UserHome',
    component: () => import('@/views/user/Home.vue'),
    meta: { title: '首页', requiresAuth: true, role: 0 }
  },
  {
    path: '/books',
    name: 'UserBooks',
    component: () => import('@/views/user/Books.vue'),
    meta: { title: '图书列表', requiresAuth: true, role: 0 }
  },
  {
    path: '/cart',
    name: 'UserCart',
    component: () => import('@/views/user/Cart.vue'),
    meta: { title: '购物车', requiresAuth: true, role: 0 }
  },
  {
    path: '/checkout',
    name: 'UserCheckout',
    component: () => import('@/views/user/Checkout.vue'),
    meta: { title: '确认订单', requiresAuth: true, role: 0 }
  },
  {
    path: '/address',
    name: 'UserAddress',
    component: () => import('@/views/user/Address.vue'),
    meta: { title: '收货地址', requiresAuth: true, role: 0 }
  },
  {
    path: '/orders',
    name: 'UserOrders',
    component: () => import('@/views/user/Orders.vue'),
    meta: { title: '我的订单', requiresAuth: true, role: 0 }
  },
  {
    path: '/reviews',
    name: 'UserReviews',
    component: () => import('@/views/user/Reviews.vue'),
    meta: { title: '我的评价', requiresAuth: true, role: 0 }
  },
  {
    path: '/favorites',
    name: 'UserFavorites',
    component: () => import('@/views/user/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true, role: 0 }
  },
  {
    path: '/recommend',
    name: 'UserRecommend',
    component: () => import('@/views/user/Recommend.vue'),
    meta: { title: '智能推荐', requiresAuth: true, role: 0 }
  },
  {
    path: '/history',
    name: 'UserHistory',
    component: () => import('@/views/user/History.vue'),
    meta: { title: '浏览记录', requiresAuth: true, role: 0 }
  },
  {
    path: '/order/:id',
    name: 'UserOrderDetail',
    component: () => import('@/views/user/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true, role: 0 }
  },
  {
    path: '/book/:id',
    name: 'UserBookDetail',
    component: () => import('@/views/user/BookDetail.vue'),
    meta: { title: '图书详情', requiresAuth: true, role: 0 }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true, role: 0 }
  },
  // 管理员端路由
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '管理员登录', requiresAuth: false }
  },
  {
    path: '/admin/home',
    name: 'AdminHome',
    component: () => import('@/views/admin/Home.vue'),
    meta: { title: '管理后台', requiresAuth: true, role: 1 }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/views/admin/UserManage.vue'),
    meta: { title: '用户管理', requiresAuth: true, role: 1 }
  },
  {
    path: '/admin/books',
    name: 'AdminBooks',
    component: () => import('@/views/admin/BookManage.vue'),
    meta: { title: '图书管理', requiresAuth: true, role: 1 }
  },
  {
    path: '/admin/categories',
    name: 'AdminCategories',
    component: () => import('@/views/admin/CategoryManage.vue'),
    meta: { title: '分类管理', requiresAuth: true, role: 1 }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('@/views/admin/OrderManage.vue'),
    meta: { title: '订单管理', requiresAuth: true, role: 1 }
  },
  {
    path: '/admin/statistics',
    name: 'AdminStatistics',
    component: () => import('@/views/admin/Statistics.vue'),
    meta: { title: '数据统计', requiresAuth: true, role: 1 }
  },
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 购书推荐系统` : '购书推荐系统'

  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  // 不需要登录的页面
  if (!to.meta.requiresAuth) {
    // 如果已登录，跳转到对应首页
    if (token && (to.path === '/login' || to.path === '/register')) {
      next(userInfo.role === 1 ? '/admin/home' : '/home')
      return
    }
    if (token && to.path === '/admin/login') {
      next(userInfo.role === 1 ? '/admin/home' : '/home')
      return
    }
    next()
    return
  }

  // 需要登录的页面
  if (!token) {
    // 未登录，跳转到登录页
    if (to.path.startsWith('/admin')) {
      next('/admin/login')
    } else {
      next('/login')
    }
    return
  }

  // 验证角色权限
  if (to.meta.role !== undefined && userInfo.role !== to.meta.role) {
    // 角色不匹配，跳转到对应首页
    next(userInfo.role === 1 ? '/admin/home' : '/home')
    return
  }

  next()
})

export default router
