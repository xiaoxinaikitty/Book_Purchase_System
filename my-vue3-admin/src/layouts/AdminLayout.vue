<template>
  <div class="admin-app admin-shell">
    <div v-if="isMobile && mobileOpen" class="sidebar-mask" @click="mobileOpen = false"></div>

    <aside
      class="admin-sidebar"
      :class="{
        collapsed: !isMobile && isCollapsed,
        'mobile-open': isMobile && mobileOpen,
        mobile: isMobile
      }"
    >
      <div class="sidebar-brand">
        <div class="brand-logo">
          <el-icon :size="22"><DataAnalysis /></el-icon>
        </div>
        <div v-show="!isCollapsed || isMobile" class="brand-text">
          <strong>BookOps</strong>
          <span>管理中心</span>
        </div>
      </div>

      <el-scrollbar class="sidebar-scroll">
        <nav class="menu-list">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="menu-item"
            :class="{ active: route.path === item.path }"
            @click="onMenuClick"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <span v-show="!isCollapsed || isMobile">{{ item.label }}</span>
          </router-link>
        </nav>
      </el-scrollbar>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-left">
          <el-button
            text
            :icon="topbarToggleIcon"
            class="toggle-btn"
            @click="toggleSidebar"
          />
          <div class="route-meta">
            <div class="route-title">{{ currentTitle }}</div>
            <div class="route-desc">{{ currentDateText }}</div>
          </div>
        </div>

        <div class="topbar-right">
          <div class="quick-pill">管理员模式</div>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="admin-account">
              <el-avatar :size="36" class="avatar">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="account-text">
                <strong>{{ displayName }}</strong>
                <span>{{ userStore.username }}</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">前往首页</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  House,
  Reading,
  Grid,
  UserFilled,
  ShoppingBag,
  TrendCharts,
  Setting,
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
  { path: '/admin/home', label: '控制台', icon: House },
  { path: '/admin/books', label: '图书管理', icon: Reading },
  { path: '/admin/categories', label: '分类管理', icon: Grid },
  { path: '/admin/users', label: '用户管理', icon: UserFilled },
  { path: '/admin/orders', label: '订单管理', icon: ShoppingBag },
  { path: '/admin/statistics', label: '数据统计', icon: TrendCharts },
  { path: '/admin/recommend-config', label: '推荐配置', icon: Setting }
]

const displayName = computed(() => userStore.nickname || userStore.username || 'Admin')
const currentTitle = computed(() => route.meta.title || '管理后台')
const topbarToggleIcon = computed(() => (isMobile.value ? Expand : isCollapsed.value ? Expand : Fold))
const currentDateText = computed(() => {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  return `${y}-${m}-${d} · 运营概览`
})

const onMenuClick = () => {
  if (isMobile.value) {
    mobileOpen.value = false
  }
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
  if (!isMobile.value) {
    mobileOpen.value = false
  }
}

const handleCommand = async (command) => {
  if (command === 'home') {
    router.push('/home')
    return
  }

  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出当前管理员账号吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
      router.push('/admin/login')
    } catch {
      // ignore cancel
    }
  }
}

watch(
  () => route.path,
  () => {
    if (isMobile.value) {
      mobileOpen.value = false
    }
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
.admin-shell {
  display: flex;
}

.sidebar-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  z-index: 120;
}

.admin-sidebar {
  width: 250px;
  min-width: 250px;
  height: 100vh;
  position: sticky;
  top: 0;
  background: linear-gradient(180deg, #0c1d3b, #102a55);
  color: #d8e4ff;
  transition: width 0.24s ease;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  z-index: 130;
}

.admin-sidebar.collapsed {
  width: 78px;
  min-width: 78px;
}

.admin-sidebar.mobile {
  position: fixed;
  left: 0;
  transform: translateX(-100%);
  transition: transform 0.22s ease;
}

.admin-sidebar.mobile.mobile-open {
  transform: translateX(0);
}

.sidebar-brand {
  min-height: 66px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-logo {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, #00a3ff, #2dd4bf);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
}

.brand-text strong {
  color: #fff;
  font-size: 15px;
}

.brand-text span {
  font-size: 12px;
  color: #9bb0d5;
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
  border-radius: 10px;
  color: #d8e4ff;
  text-decoration: none;
  min-height: 42px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 12px;
  transition: all 0.2s;
}

.admin-sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 0;
}

.menu-item:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.08);
}

.menu-item.active {
  color: #ffffff;
  background: linear-gradient(90deg, rgba(22, 119, 255, 0.36), rgba(0, 181, 120, 0.18));
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.15);
}

.admin-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.admin-topbar {
  min-height: 66px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--admin-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
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

.route-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.route-desc {
  font-size: 12px;
  color: var(--admin-text-light);
}

.quick-pill {
  padding: 6px 12px;
  border-radius: 999px;
  background: var(--admin-primary-soft);
  color: var(--admin-primary);
  font-size: 12px;
  font-weight: 600;
}

.admin-account {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.avatar {
  background: linear-gradient(135deg, #0ea5e9, #1677ff);
  color: #fff;
}

.account-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.account-text strong {
  font-size: 13px;
  color: #0f172a;
}

.account-text span {
  font-size: 12px;
  color: var(--admin-text-light);
}

.admin-content {
  padding: 18px;
}

@media (max-width: 980px) {
  .admin-main {
    width: 100%;
  }

  .admin-topbar {
    padding: 0 12px;
  }

  .quick-pill,
  .account-text {
    display: none;
  }

  .admin-content {
    padding: 12px;
  }
}
</style>
