<template>
  <div class="user-home">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="navbar-container">
        <div class="navbar-brand">
          <el-icon :size="28"><Reading /></el-icon>
          <span>购书推荐系统</span>
        </div>

        <nav class="navbar-menu">
          <router-link to="/home" class="menu-item active">首页</router-link>
          <router-link to="/books" class="menu-item">图书分类</router-link>
          <a href="#" class="menu-item">推荐</a>
          <router-link to="/cart" class="menu-item">购物车</router-link>
        </nav>

        <div class="navbar-actions">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.avatar || undefined">
                {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.nickname || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><Document /></el-icon>我的订单
                </el-dropdown-item>
                <el-dropdown-item command="reviews">
                  <el-icon><ChatLineRound /></el-icon>我的评价
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>我的收藏
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容区 -->
    <main class="main-content">
      <!-- 欢迎横幅 -->
      <section class="welcome-banner">
        <div class="banner-content">
          <h1>欢迎回来，{{ userStore.nickname || userStore.username }}！</h1>
          <p>发现好书，开启智慧阅读之旅</p>
          <el-button type="primary" size="large" round>
            探索推荐
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="banner-illustration">
          <el-icon :size="200" color="rgba(255,255,255,0.2)"><Reading /></el-icon>
        </div>
      </section>

      <!-- 功能入口 -->
      <section class="quick-actions">
        <div class="action-card">
          <div class="action-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
            <el-icon :size="32"><Search /></el-icon>
          </div>
          <h3>搜索图书</h3>
          <p>海量图书任你搜索</p>
        </div>
        <div class="action-card">
          <div class="action-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c);">
            <el-icon :size="32"><TrendCharts /></el-icon>
          </div>
          <h3>智能推荐</h3>
          <p>KNN算法精准推荐</p>
        </div>
        <div class="action-card">
          <div class="action-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe);">
            <el-icon :size="32"><ShoppingCart /></el-icon>
          </div>
          <h3>购物车</h3>
          <p>快速便捷购书体验</p>
        </div>
        <div class="action-card">
          <div class="action-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7);">
            <el-icon :size="32"><Collection /></el-icon>
          </div>
          <h3>我的收藏</h3>
          <p>收藏喜爱的图书</p>
        </div>
      </section>

      <!-- 占位提示 -->
      <section class="placeholder-section">
        <el-empty description="首页内容开发中，敬请期待...">
          <el-button type="primary" @click="router.push('/books')">浏览图书</el-button>
        </el-empty>
      </section>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© 2024 购书推荐系统 · 基于 KNN 算法的智能图书推荐平台</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Reading, ArrowDown, ArrowRight, User, Document, Star, ChatLineRound,
  SwitchButton, Search, TrendCharts, ShoppingCart, Collection
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'reviews':
      router.push('/reviews')
      break
    case 'favorites':
      // router.push('/favorites')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logout()
      } catch {
        // 取消退出
      }
      break
  }
}
</script>

<style scoped>
.user-home {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

/* 导航栏 */
.navbar {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.navbar-menu {
  display: flex;
  gap: 8px;
}

.menu-item {
  padding: 8px 20px;
  color: #64748b;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
}

.menu-item:hover,
.menu-item.active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.navbar-actions {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 24px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f1f5f9;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

/* 主体内容 */
.main-content {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 48px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
}

.banner-content {
  color: #fff;
  position: relative;
  z-index: 1;
}

.banner-content h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
}

.banner-content p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.banner-content .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
}

.banner-content .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
}

.banner-illustration {
  position: absolute;
  right: 40px;
  opacity: 0.3;
}

/* 功能入口 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.action-card {
  background: #fff;
  border-radius: 16px;
  padding: 28px 24px;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: #fff;
}

.action-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 6px;
}

.action-card p {
  font-size: 13px;
  color: #94a3b8;
}

/* 占位区域 */
.placeholder-section {
  background: #fff;
  border-radius: 16px;
  padding: 60px 24px;
}

/* 底部 */
.footer {
  background: #fff;
  text-align: center;
  padding: 20px;
  color: #94a3b8;
  font-size: 13px;
  border-top: 1px solid #e2e8f0;
}

/* 响应式 */
@media (max-width: 992px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }

  .navbar-menu {
    display: none;
  }
}

@media (max-width: 640px) {
  .quick-actions {
    grid-template-columns: 1fr;
  }

  .welcome-banner {
    padding: 32px 24px;
  }

  .banner-content h1 {
    font-size: 24px;
  }

  .banner-illustration {
    display: none;
  }
}
</style>
