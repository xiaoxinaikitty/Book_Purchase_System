<template>
  <div class="recommend-page">
    <header class="page-hero">
      <div class="hero-content">
        <h1>智能推荐</h1>
        <p>基于 KNN 算法的个性化选书助手</p>
        <div class="hero-actions">
          <el-button type="primary" round @click="router.push('/books')">去图书库</el-button>
          <el-button round @click="loadAll">刷新推荐</el-button>
        </div>
      </div>
      <div class="hero-visual">
        <div class="circle"></div>
        <div class="circle"></div>
      </div>
    </header>

    <section class="section" v-loading="loading.personal">
      <div class="section-title">
        <div>
          <h2>为你推荐</h2>
          <p>根据相似用户评分与偏好生成</p>
        </div>
      </div>
      <div class="grid">
        <div v-for="book in personalList" :key="book.id" class="book-card" @click="goDetail(book.id)">
          <img :src="book.coverImage || defaultCover" alt="cover" />
          <div class="info">
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

    <section class="section" v-loading="loading.guess">
      <div class="section-title">
        <div>
          <h2>猜你喜欢</h2>
          <p>综合行为与热度的混合推荐</p>
        </div>
      </div>
      <div class="grid">
        <div v-for="book in guessList" :key="book.id" class="book-card" @click="goDetail(book.id)">
          <img :src="book.coverImage || defaultCover" alt="cover" />
          <div class="info">
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

    <section class="section dual">
      <el-card class="panel" shadow="never" v-loading="loading.hot">
        <div class="panel-title">
          <h3>热门榜单</h3>
          <span>销量领先</span>
        </div>
        <div class="panel-list">
          <div v-for="book in hotList" :key="book.id" class="panel-item" @click="goDetail(book.id)">
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="panel-info">
              <div class="title">{{ book.title }}</div>
              <div class="meta">¥{{ formatPrice(book.price) }}</div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="panel" shadow="never" v-loading="loading.new">
        <div class="panel-title">
          <h3>新书速递</h3>
          <span>最新上架</span>
        </div>
        <div class="panel-list">
          <div v-for="book in newList" :key="book.id" class="panel-item" @click="goDetail(book.id)">
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="panel-info">
              <div class="title">{{ book.title }}</div>
              <div class="meta">¥{{ formatPrice(book.price) }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPersonalRecommend,
  getGuessRecommend,
  getHotRecommend,
  getNewRecommend
} from '@/api/recommend'

const router = useRouter()
const defaultCover = '/vite.svg'

const personalList = ref([])
const guessList = ref([])
const hotList = ref([])
const newList = ref([])

const loading = reactive({
  personal: false,
  guess: false,
  hot: false,
  new: false
})

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const goDetail = (id) => {
  router.push(`/book/${id}`)
}

const loadPersonal = async () => {
  loading.personal = true
  try {
    const res = await getPersonalRecommend({ limit: 8 })
    personalList.value = res.data || []
  } catch (error) {
    console.error('获取个性化推荐失败:', error)
  } finally {
    loading.personal = false
  }
}

const loadGuess = async () => {
  loading.guess = true
  try {
    const res = await getGuessRecommend({ limit: 8 })
    guessList.value = res.data || []
  } catch (error) {
    console.error('获取猜你喜欢失败:', error)
  } finally {
    loading.guess = false
  }
}

const loadHot = async () => {
  loading.hot = true
  try {
    const res = await getHotRecommend({ limit: 6 })
    hotList.value = res.data || []
  } catch (error) {
    console.error('获取热门推荐失败:', error)
  } finally {
    loading.hot = false
  }
}

const loadNew = async () => {
  loading.new = true
  try {
    const res = await getNewRecommend({ limit: 6 })
    newList.value = res.data || []
  } catch (error) {
    console.error('获取新书推荐失败:', error)
  } finally {
    loading.new = false
  }
}

const loadAll = () => {
  loadPersonal()
  loadGuess()
  loadHot()
  loadNew()
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped>
.recommend-page {
  min-height: 100vh;
  background: #f5f7fb;
  padding-bottom: 40px;
}

.page-hero {
  position: relative;
  overflow: hidden;
  padding: 52px 32px;
  background: linear-gradient(120deg, #0f172a, #1e293b, #4338ca);
  color: #fff;
  border-bottom-left-radius: 32px;
  border-bottom-right-radius: 32px;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.hero-content h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
}

.hero-content p {
  font-size: 15px;
  opacity: 0.85;
  margin-bottom: 24px;
}

.hero-actions {
  display: flex;
  gap: 12px;
}

.hero-visual .circle {
  position: absolute;
  right: 80px;
  top: -80px;
  width: 240px;
  height: 240px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  filter: blur(0.3px);
}

.hero-visual .circle:last-child {
  right: -40px;
  top: 80px;
  width: 200px;
  height: 200px;
  background: rgba(99, 102, 241, 0.4);
}

.section {
  max-width: 1200px;
  margin: 24px auto 0;
  padding: 0 24px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 16px;
}

.section-title h2 {
  font-size: 20px;
  color: #111827;
}

.section-title p {
  font-size: 12px;
  color: #6b7280;
}

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.book-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 8px 18px rgba(17, 24, 39, 0.08);
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 26px rgba(17, 24, 39, 0.12);
}

.book-card img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.info {
  padding: 12px;
}

.title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}

.author {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 10px;
}

.meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #4b5563;
}

.dual {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.panel {
  border-radius: 16px;
  border: none;
  background: #fff;
}

.panel-title {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 12px;
}

.panel-title h3 {
  font-size: 16px;
  color: #111827;
}

.panel-title span {
  font-size: 12px;
  color: #9ca3af;
}

.panel-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.panel-item {
  display: flex;
  gap: 10px;
  padding: 8px;
  border-radius: 12px;
  background: #f9fafb;
  cursor: pointer;
}

.panel-item img {
  width: 64px;
  height: 90px;
  object-fit: cover;
  border-radius: 10px;
}

.panel-info .title {
  font-size: 13px;
  margin-bottom: 6px;
}

.panel-info .meta {
  font-size: 12px;
  color: #6b7280;
}

@media (max-width: 1200px) {
  .grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 960px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .dual {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    flex-direction: column;
  }
}
</style>
