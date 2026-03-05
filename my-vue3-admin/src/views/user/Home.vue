<template>
  <div class="home-page">
    <section class="hero user-card">
      <div class="hero-content">
        <div class="hero-text">
          <div class="badge">智能推荐已启动</div>
          <h1>欢迎回来，{{ userStore.nickname || userStore.username }}</h1>
          <p>结合你的浏览、收藏与购买行为，持续发现更适合你的高质量图书。</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="router.push('/recommend')">
              进入推荐页
            </el-button>
            <el-button size="large" @click="router.push('/books')">浏览图书广场</el-button>
          </div>
        </div>
        <div class="hero-kpis">
          <div class="kpi">
            <span>热门图书</span>
            <strong>{{ hotBooks.length }}</strong>
          </div>
          <div class="kpi">
            <span>新书上架</span>
            <strong>{{ newBooks.length }}</strong>
          </div>
          <div class="kpi">
            <span>精选推荐</span>
            <strong>{{ featuredBooks.length }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="quick user-grid-4">
      <div class="entry user-card" @click="router.push('/books')">
        <div class="icon bg-blue"><el-icon><Search /></el-icon></div>
        <h3>搜索图书</h3>
        <p>按书名、作者、分类快速定位</p>
      </div>
      <div class="entry user-card" @click="router.push('/recommend')">
        <div class="icon bg-green"><el-icon><TrendCharts /></el-icon></div>
        <h3>智能推荐</h3>
        <p>KNN 算法生成个性化书单</p>
      </div>
      <div class="entry user-card" @click="router.push('/cart')">
        <div class="icon bg-orange"><el-icon><ShoppingCart /></el-icon></div>
        <h3>购物车</h3>
        <p>集中管理待购买商品</p>
      </div>
      <div class="entry user-card" @click="router.push('/orders')">
        <div class="icon bg-purple"><el-icon><Document /></el-icon></div>
        <h3>我的订单</h3>
        <p>查看订单状态和物流进度</p>
      </div>
    </section>

    <section class="book-section user-card user-card-inner">
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
              <span class="price">¥{{ formatPrice(book.price) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="book-section user-card user-card-inner">
      <div class="section-header">
        <div>
          <h2>新书上架</h2>
          <p>最近出版推荐</p>
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
              <span class="price">¥{{ formatPrice(book.price) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="book-section user-card user-card-inner">
      <div class="section-header">
        <div>
          <h2>精选图书</h2>
          <p>为你挑选的人气书单</p>
        </div>
        <el-button text @click="router.push('/books')">查看更多</el-button>
      </div>
      <div class="book-grid" v-loading="loading.feature">
        <div
          v-for="book in featuredBooks"
          :key="book.id"
          class="book-card"
          @click="goDetail(book.id)"
        >
          <img :src="book.coverImage || defaultCover" alt="cover" />
          <div class="book-info">
            <div class="title">{{ book.title }}</div>
            <div class="author">{{ book.author || '未知作者' }}</div>
            <div class="meta">
              <span>评分 {{ book.rating || 0 }}</span>
              <span class="price">¥{{ formatPrice(book.price) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, TrendCharts, ShoppingCart, Document } from '@element-plus/icons-vue'
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

const formatPrice = (price) => Number(price || 0).toFixed(2)
const goDetail = (id) => router.push(`/book/${id}`)

const loadHotBooks = async () => {
  loading.hot = true
  try {
    const res = await getHotBooks(8)
    hotBooks.value = res.data || []
  } finally {
    loading.hot = false
  }
}

const loadNewBooks = async () => {
  loading.new = true
  try {
    const res = await getNewBooks(8)
    newBooks.value = res.data || []
  } finally {
    loading.new = false
  }
}

const loadFeaturedBooks = async () => {
  loading.feature = true
  try {
    const res = await getBookList({ page: 1, size: 8 })
    featuredBooks.value = res.data.records || []
  } finally {
    loading.feature = false
  }
}

onMounted(() => {
  loadHotBooks()
  loadNewBooks()
  loadFeaturedBooks()
})
</script>

<style scoped>
.home-page {
  display: grid;
  gap: 16px;
}

.hero {
  background: linear-gradient(130deg, #0f6fff, #00b578);
  color: #fff;
  border: none;
}

.hero-content {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 16px;
  padding: 28px;
}

.badge {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  font-size: 12px;
  margin-bottom: 14px;
}

.hero-text h1 {
  font-size: 34px;
  line-height: 1.2;
  margin-bottom: 12px;
}

.hero-text p {
  opacity: 0.95;
  max-width: 520px;
}

.hero-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.hero-kpis {
  display: grid;
  gap: 10px;
}

.kpi {
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 14px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(8px);
}

.kpi span {
  font-size: 12px;
  opacity: 0.88;
}

.kpi strong {
  margin-top: 4px;
  display: block;
  font-size: 30px;
  line-height: 1;
}

.entry {
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.entry:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 34px rgba(15, 23, 42, 0.1);
}

.icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  color: #fff;
  display: grid;
  place-items: center;
  font-size: 22px;
  margin-bottom: 14px;
}

.bg-blue {
  background: linear-gradient(135deg, #0f6fff, #0284ff);
}

.bg-green {
  background: linear-gradient(135deg, #00b578, #22c55e);
}

.bg-orange {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.bg-purple {
  background: linear-gradient(135deg, #7c3aed, #6366f1);
}

.entry h3 {
  font-size: 16px;
  margin-bottom: 6px;
}

.entry p {
  color: var(--user-text-secondary);
  font-size: 13px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.section-header h2 {
  font-size: 18px;
}

.section-header p {
  font-size: 12px;
  color: var(--user-text-light);
  margin-top: 4px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.book-card {
  border: 1px solid #edf2f8;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  background: #fbfdff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.book-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.08);
}

.book-card img {
  width: 100%;
  height: 176px;
  object-fit: cover;
}

.book-info {
  padding: 12px;
}

.book-info .title {
  font-size: 14px;
  font-weight: 700;
}

.book-info .author {
  margin-top: 6px;
  font-size: 12px;
  color: var(--user-text-light);
}

.book-info .meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--user-text-secondary);
}

.price {
  color: #f97316;
  font-weight: 700;
}

@media (max-width: 1180px) {
  .book-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 920px) {
  .hero-content {
    grid-template-columns: minmax(0, 1fr);
  }

  .book-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 680px) {
  .hero-content {
    padding: 20px;
  }

  .hero-text h1 {
    font-size: 26px;
  }

  .book-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
