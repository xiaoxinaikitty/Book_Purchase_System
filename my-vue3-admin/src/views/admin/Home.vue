<template>
  <div class="admin-home">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <el-icon :size="28"><Monitor /></el-icon>
        <span v-show="!isCollapsed" class="logo-text">管理后台</span>
      </div>
      
      <nav class="sidebar-menu">
        <div class="menu-item active">
          <el-icon><HomeFilled /></el-icon>
          <span v-show="!isCollapsed">控制台</span>
        </div>
        <div class="menu-item">
          <el-icon><Reading /></el-icon>
          <span v-show="!isCollapsed">图书管理</span>
        </div>
        <div class="menu-item">
          <el-icon><Folder /></el-icon>
          <span v-show="!isCollapsed">分类管理</span>
        </div>
        <div class="menu-item">
          <el-icon><UserFilled /></el-icon>
          <span v-show="!isCollapsed">用户管理</span>
        </div>
        <div class="menu-item">
          <el-icon><Document /></el-icon>
          <span v-show="!isCollapsed">订单管理</span>
        </div>
        <div class="menu-item">
          <el-icon><TrendCharts /></el-icon>
          <span v-show="!isCollapsed">数据统计</span>
        </div>
        <div class="menu-item">
          <el-icon><Setting /></el-icon>
          <span v-show="!isCollapsed">系统设置</span>
        </div>
      </nav>
    </aside>
    
    <!-- 主内容区 -->
    <div class="main-wrapper">
      <!-- 顶部栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <el-button 
            text 
            :icon="isCollapsed ? Expand : Fold" 
            @click="isCollapsed = !isCollapsed"
          />
          <span class="page-title">控制台</span>
        </div>
        
        <div class="topbar-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="admin-info">
              <el-avatar :size="36">
                {{ userStore.nickname?.charAt(0) || 'A' }}
              </el-avatar>
              <div class="admin-text">
                <span class="admin-name">{{ userStore.nickname || userStore.username }}</span>
                <span class="admin-role">系统管理员</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 主内容 -->
      <main class="main-content">
        <!-- 统计卡片 -->
        <div class="stat-cards">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
              <el-icon :size="28"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">1,234</span>
              <span class="stat-label">用户总数</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c);">
              <el-icon :size="28"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">5,678</span>
              <span class="stat-label">图书总数</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe);">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">892</span>
              <span class="stat-label">订单总数</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7);">
              <el-icon :size="28"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">¥86,420</span>
              <span class="stat-label">销售总额</span>
            </div>
          </div>
        </div>
        
        <!-- 占位内容 -->
        <div class="content-placeholder">
          <el-empty description="后台功能开发中，敬请期待...">
            <template #image>
              <el-icon :size="100" color="#c0c4cc"><Monitor /></el-icon>
            </template>
          </el-empty>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { 
  Monitor, HomeFilled, Reading, Folder, UserFilled, Document, 
  TrendCharts, Setting, Fold, Expand, ArrowDown, User, SwitchButton, Coin 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // router.push('/admin/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logout()
        router.push('/admin/login')
      } catch {
        // 取消退出
      }
      break
  }
}
</script>

<style scoped>
.admin-home {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: #001529;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
}

.sidebar-menu {
  flex: 1;
  padding: 16px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 14px;
}

.menu-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
}

.menu-item.active {
  color: #fff;
  background: #1890ff;
}

/* 主内容区 */
.main-wrapper {
  flex: 1;
  margin-left: 240px;
  transition: margin-left 0.3s;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed ~ .main-wrapper {
  margin-left: 64px;
}

/* 顶部栏 */
.topbar {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 99;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.admin-info:hover {
  background: #f5f5f5;
}

.admin-text {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.admin-role {
  font-size: 12px;
  color: #9ca3af;
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: 24px;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 14px;
  color: #9ca3af;
  margin-top: 4px;
}

/* 占位内容 */
.content-placeholder {
  background: #fff;
  border-radius: 12px;
  padding: 80px 24px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 64px;
  }
  
  .sidebar .logo-text,
  .sidebar .menu-item span {
    display: none;
  }
  
  .sidebar .menu-item {
    justify-content: center;
    padding: 14px;
  }
  
  .main-wrapper {
    margin-left: 64px;
  }
  
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>

