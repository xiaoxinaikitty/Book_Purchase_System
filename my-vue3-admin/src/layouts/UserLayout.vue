<template>
  <div class="user-shell">
    <div v-if="isMobile && mobileOpen" class="sidebar-mask" @click="mobileOpen = false"></div>

    <aside
      class="user-sidebar"
      :class="{
        collapsed: !isMobile && isCollapsed,
        mobile: isMobile,
        'mobile-open': isMobile && mobileOpen
      }"
    >
      <div class="sidebar-brand">
        <div class="brand-logo">
          <el-icon :size="20"><Reading /></el-icon>
        </div>
        <div v-show="!isCollapsed || isMobile" class="brand-text">
          <strong>BookFlow</strong>
          <span>用户中心</span>
        </div>
      </div>

      <el-scrollbar class="sidebar-scroll">
        <nav class="menu-list">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="menu-item"
            :class="{ active: isActive(item.path) }"
            @click="onMenuClick"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <span v-show="!isCollapsed || isMobile">{{ item.label }}</span>
          </router-link>
        </nav>
      </el-scrollbar>
    </aside>

    <div class="user-main">
      <header class="user-topbar">
        <div class="topbar-left">
          <el-button text :icon="toggleIcon" class="toggle-btn" @click="toggleSidebar" />
          <div class="route-meta">
            <strong>{{ routeTitle }}</strong>
            <span>{{ todayText }}</span>
          </div>
        </div>

        <div class="topbar-right">
          <el-tag effect="plain" type="primary">用户端</el-tag>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="34" :src="userStore.avatar || undefined" class="user-avatar">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-meta">
                <strong>{{ displayName }}</strong>
                <span>{{ userStore.username }}</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="cart">购物车</el-dropdown-item>
                <el-dropdown-item command="admin">管理员入口</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="user-content">
        <router-view v-slot="{ Component }">
          <component v-if="Component" :is="Component" :key="route.fullPath" />
          <div v-else class="empty-state">
            <h3>页面未匹配</h3>
            <p>当前路径：{{ route.fullPath }}</p>
          </div>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Reading,
  House,
  Search,
  TrendCharts,
  Document,
  Star,
  Clock,
  Bell,
  ShoppingCart,
  User,
  ArrowDown,
  Fold,
  Expand
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)
const isMobile = ref(false)
const mobileOpen = ref(false)

const menuItems = [
  { path: '/home', label: '首页', icon: House },
  { path: '/books', label: '图书广场', icon: Search },
  { path: '/recommend', label: '智能推荐', icon: TrendCharts },
  { path: '/orders', label: '我的订单', icon: Document },
  { path: '/notices', label: '公告中心', icon: Bell },
  { path: '/favorites', label: '收藏夹', icon: Star },
  { path: '/history', label: '浏览记录', icon: Clock },
  { path: '/cart', label: '购物车', icon: ShoppingCart },
  { path: '/profile', label: '个人中心', icon: User }
]

const displayName = computed(() => userStore.nickname || userStore.username || '用户')
const routeTitle = computed(() => route.meta.title || '用户中心')
const toggleIcon = computed(() => (isMobile.value ? Expand : isCollapsed.value ? Expand : Fold))

const todayText = computed(() => {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  return `${y}-${m}-${d} · 阅读与购物`
})

const isActive = (path) => {
  if (path === '/home') return route.path === '/home'
  return route.path.startsWith(path)
}

const onMenuClick = () => {
  if (isMobile.value) mobileOpen.value = false
}

const toggleSidebar = () => {
  if (isMobile.value) {
    mobileOpen.value = !mobileOpen.value
    return
  }
  isCollapsed.value = !isCollapsed.value
}

const syncScreenMode = () => {
  isMobile.value = window.innerWidth <= 980
  if (!isMobile.value) mobileOpen.value = false
}

const handleCommand = async (command) => {
  const mapping = {
    profile: '/profile',
    orders: '/orders',
    cart: '/cart',
    admin: '/admin/login'
  }
  if (mapping[command]) {
    router.push(mapping[command])
    return
  }

  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出当前账号吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
    } catch {
      // ignore cancel
    }
  }
}

watch(
  () => route.path,
  () => {
    if (isMobile.value) mobileOpen.value = false
  }
)

onMounted(() => {
  syncScreenMode()
  window.addEventListener('resize', syncScreenMode)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', syncScreenMode)
})
</script>

<style scoped>
.user-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: row;
  background: linear-gradient(180deg, #f4f7fb 0%, #edf2f8 100%);
}

.sidebar-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.36);
  z-index: 120;
}

.user-sidebar {
  width: 248px;
  min-width: 248px;
  height: 100vh;
  position: sticky;
  top: 0;
  background: linear-gradient(180deg, #10284f, #15376a);
  color: #d6e4ff;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  transition: width 0.24s ease;
  display: flex;
  flex-direction: column;
  z-index: 130;
}

.user-sidebar.collapsed {
  width: 78px;
  min-width: 78px;
}

.user-sidebar.mobile {
  position: fixed;
  left: 0;
  transform: translateX(-100%);
  transition: transform 0.22s ease;
}

.user-sidebar.mobile.mobile-open {
  transform: translateX(0);
}

.sidebar-brand {
  min-height: 66px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, #0f6fff, #00b578);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-text strong {
  color: #fff;
  font-size: 15px;
}

.brand-text span {
  color: #a8bfeb;
  font-size: 12px;
}

.sidebar-scroll {
  height: calc(100vh - 66px);
}

.menu-list {
  padding: 12px;
  display: grid;
  gap: 6px;
}

.menu-item {
  min-height: 42px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-radius: 10px;
  padding: 0 12px;
  text-decoration: none;
  color: #d6e4ff;
  transition: all 0.2s;
}

.user-sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 0;
}

.menu-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.menu-item.active {
  color: #fff;
  background: linear-gradient(90deg, rgba(15, 111, 255, 0.34), rgba(0, 181, 120, 0.18));
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.16);
}

.user-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.user-topbar {
  min-height: 66px;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e6ebf3;
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.topbar-left,
.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.route-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.route-meta strong {
  color: #0f172a;
  font-size: 18px;
}

.route-meta span {
  color: #8a95a6;
  font-size: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #0f6fff, #00b578);
  color: #fff;
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.user-meta strong {
  color: #0f172a;
  font-size: 13px;
}

.user-meta span {
  color: #8a95a6;
  font-size: 12px;
}

.user-content {
  flex: 1;
  padding: 18px;
  min-width: 0;
  overflow-x: hidden;
}

.empty-state {
  min-height: 260px;
  border: 1px dashed #cdd8ea;
  border-radius: 14px;
  background: #fff;
  display: grid;
  place-content: center;
  text-align: center;
  color: #5f6b7a;
}

.empty-state h3 {
  color: #0f172a;
  margin-bottom: 8px;
}

@media (max-width: 980px) {
  .user-topbar {
    padding: 0 12px;
  }

  .user-meta {
    display: none;
  }

  .user-content {
    padding: 12px;
  }
}
</style>
