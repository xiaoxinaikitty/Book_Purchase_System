import { createRouter, createWebHistory } from 'vue-router'

const routes = [
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
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: { requiresAuth: true, role: 0 },
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'books',
        name: 'UserBooks',
        component: () => import('@/views/user/Books.vue'),
        meta: { title: '图书列表' }
      },
      {
        path: 'cart',
        name: 'UserCart',
        component: () => import('@/views/user/Cart.vue'),
        meta: { title: '购物车' }
      },
      {
        path: 'checkout',
        name: 'UserCheckout',
        component: () => import('@/views/user/Checkout.vue'),
        meta: { title: '确认订单' }
      },
      {
        path: 'address',
        name: 'UserAddress',
        component: () => import('@/views/user/Address.vue'),
        meta: { title: '收货地址' }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/Orders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'reviews',
        name: 'UserReviews',
        component: () => import('@/views/user/Reviews.vue'),
        meta: { title: '我的评价' }
      },
      {
        path: 'favorites',
        name: 'UserFavorites',
        component: () => import('@/views/user/Favorites.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'recommend',
        name: 'UserRecommend',
        component: () => import('@/views/user/Recommend.vue'),
        meta: { title: '智能推荐' }
      },
      {
        path: 'history',
        name: 'UserHistory',
        component: () => import('@/views/user/History.vue'),
        meta: { title: '浏览记录' }
      },
      {
        path: 'order/:id',
        name: 'UserOrderDetail',
        component: () => import('@/views/user/OrderDetail.vue'),
        meta: { title: '订单详情' }
      },
      {
        path: 'book/:id',
        name: 'UserBookDetail',
        component: () => import('@/views/user/BookDetail.vue'),
        meta: { title: '图书详情' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  // 管理员端路由
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '管理员登录', requiresAuth: false }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 1 },
    children: [
      {
        path: '',
        redirect: '/admin/home'
      },
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('@/views/admin/Home.vue'),
        meta: { title: '管理后台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'books',
        name: 'AdminBooks',
        component: () => import('@/views/admin/BookManage.vue'),
        meta: { title: '图书管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'recommend-config',
        name: 'AdminRecommendConfig',
        component: () => import('@/views/admin/RecommendConfig.vue'),
        meta: { title: '推荐配置' }
      }
    ]
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
  const userRole = Number(userInfo.role)
  const isAdmin = userRole === 1

  // 不需要登录的页面
  if (!to.meta.requiresAuth) {
    // 如果已登录，跳转到对应首页
    if (token && (to.path === '/login' || to.path === '/register')) {
      next(isAdmin ? '/admin/home' : '/home')
      return
    }
    if (token && to.path === '/admin/login') {
      // 只有管理员已登录时才自动跳后台；普通用户允许进入管理员登录页切换账号
      if (isAdmin) {
        next('/admin/home')
      } else {
        next()
      }
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
  if (to.meta.role !== undefined && userRole !== Number(to.meta.role)) {
    // 角色不匹配，跳转到对应首页
    next(isAdmin ? '/admin/home' : '/home')
    return
  }

  next()
})

export default router
