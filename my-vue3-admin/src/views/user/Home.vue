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
          <router-link to="/recommend" class="menu-item">智能推荐</router-link>
          <router-link to="/favorites" class="menu-item">我的收藏</router-link>
          <router-link to="/history" class="menu-item">浏览记录</router-link>
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
                <el-dropdown-item command="recommend">
                  <el-icon><Compass /></el-icon>智能推荐
                </el-dropdown-item>
                <el-dropdown-item command="reviews">
                  <el-icon><ChatLineRound /></el-icon>我的评价
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="history">
                  <el-icon><Clock /></el-icon>浏览记录
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
          <div class="banner-actions">
            <el-button type="primary" size="large" round @click="router.push('/recommend')">
              探索推荐
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" round @click="router.push('/books')">逛书城</el-button>
          </div>
        </div>
        <div class="banner-illustration">
          <el-icon :size="200" color="rgba(255,255,255,0.2)"><Reading /></el-icon>
        </div>
      </section>

      <!-- 功能入口 -->
      <section class="quick-actions">
        <div class="action-card" @click="router.push('/books')">
          <div class="action-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
            <el-icon :size="32"><Search /></el-icon>
          </div>
          <h3>搜索图书</h3>
          <p>海量图书任你搜索</p>
        </div>
        <div class="action-card" @click="router.push('/recommend')">
          <div class="action-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c);">
            <el-icon :size="32"><TrendCharts /></el-icon>
          </div>
          <h3>智能推荐</h3>
          <p>KNN算法精准推荐</p>
        </div>
        <div class="action-card" @click="router.push('/cart')">
          <div class="action-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe);">
            <el-icon :size="32"><ShoppingCart /></el-icon>
          </div>
          <h3>购物车</h3>
          <p>快速便捷购书体验</p>
        </div>
        <div class="action-card" @click="router.push('/favorites')">
          <div class="action-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7);">
            <el-icon :size="32"><Collection /></el-icon>
          </div>
          <h3>我的收藏</h3>
          <p>收藏喜爱的图书</p>
        </div>
      </section>

      <!-- 图书推荐展示 -->
      <section class="book-section">
        <div class="section-header">
          <div>
            <h2>热门图书</h2>
            <p>销量热度榜</p>
          </div>
          <el-button text @click="router.push('/books')">查看更多</el-button>
        </div>
        <div class="book-grid" v-loading="loading.hot">
          <div v-for="book in hotBooks" :key="book.id" class="book-card" @click="goDetail(book.id)">
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="book-info">
              <div class="title">{{ book.title }}</div>
              <div class="author">{{ book.author || '未知作者' }}</div>
              <div class="meta">
                <span>评分 {{ book.rating || 0 }}</span>
                <span>¥{{ formatPrice(book.price) }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="book-section">
        <div class="section-header">
          <div>
            <h2>新书上架</h2>
            <p>最新出版推荐</p>
          </div>
          <el-button text @click="router.push('/books')">查看更多</el-button>
        </div>
        <div class="book-grid" v-loading="loading.new">
          <div v-for="book in newBooks" :key="book.id" class="book-card" @click="goDetail(book.id)">
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="book-info">
              <div class="title">{{ book.title }}</div>
              <div class="author">{{ book.author || '未知作者' }}</div>
              <div class="meta">
                <span>评分 {{ book.rating || 0 }}</span>
                <span>¥{{ formatPrice(book.price) }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="book-section">
        <div class="section-header">
          <div>
            <h2>精选图书</h2>
            <p>为你挑选的人气书单</p>
          </div>
          <el-button text @click="router.push('/books')">查看更多</el-button>
        </div>
        <div class="book-grid" v-loading="loading.feature">
          <div v-for="book in featuredBooks" :key="book.id" class="book-card" @click="goDetail(book.id)">
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="book-info">
              <div class="title">{{ book.title }}</div>
              <div class="author">{{ book.author || '未知作者' }}</div>
              <div class="meta">
                <span>评分 {{ book.rating || 0 }}</span>
                <span>¥{{ formatPrice(book.price) }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© 2024 购书推荐系统 · 基于 KNN 算法的智能图书推荐平台</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Reading, ArrowDown, ArrowRight, User, Document, Star, ChatLineRound, Clock, Compass,
  SwitchButton, Search, TrendCharts, ShoppingCart, Collection
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getHotBooks, getNewBooks, getBookList } from '@/api/book'

const router = useRouter()
const userStore = useUserStore()
const defaultCover = '/vite.svg'

const hotBooks = ref([])
const newBooks = ref([])
const featuredBooks = ref([])

const loading = reactive({
  hot: false,
  new: false,
  feature: false
})

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const goDetail = (id) => {
  router.push(`/book/${id}`)
}

const loadHotBooks = async () => {
  loading.hot = true
  try {
    const res = await getHotBooks(8)
    hotBooks.value = res.data || []
  } catch (error) {
    console.error('获取热门图书失败:', error)
  } finally {
    loading.hot = false
  }
}

const loadNewBooks = async () => {
  loading.new = true
  try {
    const res = await getNewBooks(8)
    newBooks.value = res.data || []
  } catch (error) {
    console.error('获取新书推荐失败:', error)
  } finally {
    loading.new = false
  }
}

const loadFeaturedBooks = async () => {
  loading.feature = true
  try {
    const res = await getBookList({ page: 1, size: 8 })
    featuredBooks.value = res.data.records || []
  } catch (error) {
    console.error('获取精选图书失败:', error)
  } finally {
    loading.feature = false
  }
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'recommend':
      router.push('/recommend')
      break
    case 'reviews':
      router.push('/reviews')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'history':
      router.push('/history')
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

onMounted(() => {
  loadHotBooks()
  loadNewBooks()
  loadFeaturedBooks()
})
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

.banner-actions {
  display: flex;
  gap: 12px;
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

/* 图书展示 */
.book-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 18px;
  color: #111827;
  margin-bottom: 6px;
}

.section-header p {
  font-size: 12px;
  color: #6b7280;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.book-card {
  background: #f9fafb;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(17, 24, 39, 0.08);
}

.book-card img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.book-info {
  padding: 12px;
}

.book-info .title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}

.book-info .author {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 10px;
}

.book-info .meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #374151;
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

  .book-grid {
    grid-template-columns: repeat(2, 1fr);
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

  .banner-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .book-grid {
    grid-template-columns: 1fr;
  }
}
</style>
