<template>
  <div class="books-page">
    <header class="hero">
      <div class="hero-content">
        <h1>发现你喜欢的好书</h1>
        <p>从海量图书中筛选适合你的阅读清单</p>
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索书名 / 作者 / 出版社"
            size="large"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
        </div>
      </div>
      <div class="hero-decoration"></div>
    </header>

    <section class="highlight">
      <div class="panel">
        <div class="panel-header">
          <h3>热门图书</h3>
          <span>基于销量热度</span>
        </div>
        <div class="mini-list">
          <div
            v-for="book in hotBooks"
            :key="book.id"
            class="mini-card"
            @click="goDetail(book.id)"
          >
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="mini-title">{{ book.title }}</div>
            <div class="mini-meta">¥{{ formatPrice(book.price) }}</div>
          </div>
        </div>
      </div>
      <div class="panel">
        <div class="panel-header">
          <h3>新书上架</h3>
          <span>最新出版</span>
        </div>
        <div class="mini-list">
          <div
            v-for="book in newBooks"
            :key="book.id"
            class="mini-card"
            @click="goDetail(book.id)"
          >
            <img :src="book.coverImage || defaultCover" alt="cover" />
            <div class="mini-title">{{ book.title }}</div>
            <div class="mini-meta">¥{{ formatPrice(book.price) }}</div>
          </div>
        </div>
      </div>
    </section>

    <div class="content">
      <aside class="category">
        <h4>图书分类</h4>
        <el-scrollbar height="480px">
          <div
            class="category-item"
            :class="{ active: activeCategory === null }"
            @click="selectCategory(null)"
          >
            全部分类
          </div>
          <div
            v-for="item in categories"
            :key="item.id"
            class="category-item"
            :class="{ active: activeCategory === item.id }"
            @click="selectCategory(item.id)"
          >
            {{ item.name }}
          </div>
        </el-scrollbar>
      </aside>

      <section class="list">
        <div class="list-header">
          <div>
            <h2>图书列表</h2>
            <span>共 {{ total }} 本图书</span>
          </div>
        </div>

        <el-skeleton v-if="loading" animated :rows="6" />
        <div v-else class="grid">
          <div
            v-for="book in books"
            :key="book.id"
            class="book-card"
            @click="goDetail(book.id)"
          >
            <div class="cover">
              <img :src="book.coverImage || defaultCover" alt="cover" />
            </div>
            <div class="info">
              <div class="title">{{ book.title }}</div>
              <div class="author">{{ book.author || '未知作者' }}</div>
              <div class="meta">
                <span>¥{{ formatPrice(book.price) }}</span>
                <span>评分 {{ book.rating || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="pager">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :total="total"
            :page-size="size"
            :current-page="page"
            :page-sizes="[8, 12, 16]"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBookList, getHotBooks, getNewBooks } from '@/api/book'
import { getCategoryList } from '@/api/category'

const router = useRouter()

const keyword = ref('')
const categories = ref([])
const activeCategory = ref(null)

const books = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(false)

const hotBooks = ref([])
const newBooks = ref([])

const defaultCover = '/vite.svg'

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const loadBooks = async () => {
  loading.value = true
  try {
    const res = await getBookList({
      page: page.value,
      size: size.value,
      categoryId: activeCategory.value || undefined,
      keyword: keyword.value || undefined
    })
    books.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadHighlights = async () => {
  try {
    const [hotRes, newRes] = await Promise.all([
      getHotBooks(6),
      getNewBooks(6)
    ])
    hotBooks.value = hotRes.data || []
    newBooks.value = newRes.data || []
  } catch (error) {
    console.error('获取推荐列表失败:', error)
  }
}

const handleSearch = () => {
  page.value = 1
  loadBooks()
}

const handlePageChange = (val) => {
  page.value = val
  loadBooks()
}

const handleSizeChange = (val) => {
  size.value = val
  page.value = 1
  loadBooks()
}

const selectCategory = (id) => {
  activeCategory.value = id
  page.value = 1
  loadBooks()
}

const goDetail = (id) => {
  router.push(`/book/${id}`)
}

onMounted(() => {
  loadCategories()
  loadBooks()
  loadHighlights()
})
</script>

<style scoped>
.books-page {
  min-height: 100vh;
  background: #f6f8fb;
  padding-bottom: 40px;
}

.hero {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #2563eb 0%, #4f46e5 50%, #7c3aed 100%);
  color: #fff;
  padding: 48px 32px;
  border-bottom-left-radius: 32px;
  border-bottom-right-radius: 32px;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
}

.hero h1 {
  font-size: 34px;
  font-weight: 700;
  margin-bottom: 10px;
}

.hero p {
  font-size: 15px;
  opacity: 0.85;
  margin-bottom: 24px;
}

.search-bar {
  display: flex;
  gap: 12px;
  max-width: 520px;
}

.hero-decoration {
  position: absolute;
  top: -120px;
  right: -80px;
  width: 320px;
  height: 320px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  filter: blur(0.5px);
}

.highlight {
  max-width: 1200px;
  margin: -40px auto 24px;
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.panel {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 14px;
}

.panel-header h3 {
  font-size: 18px;
  color: #111827;
}

.panel-header span {
  font-size: 12px;
  color: #6b7280;
}

.mini-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.mini-card {
  cursor: pointer;
  padding: 8px;
  border-radius: 12px;
  transition: all 0.2s ease;
  background: #f9fafb;
}

.mini-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(17, 24, 39, 0.08);
}

.mini-card img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 10px;
}

.mini-title {
  margin-top: 8px;
  font-size: 13px;
  color: #111827;
  font-weight: 600;
}

.mini-meta {
  font-size: 12px;
  color: #6b7280;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 20px;
  padding: 0 24px;
}

.category {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  height: fit-content;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.06);
}

.category h4 {
  font-size: 16px;
  margin-bottom: 12px;
  color: #111827;
}

.category-item {
  padding: 10px 12px;
  border-radius: 10px;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-item:hover {
  background: #eef2ff;
  color: #3730a3;
}

.category-item.active {
  background: #e0e7ff;
  color: #3730a3;
  font-weight: 600;
}

.list {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.06);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.list-header h2 {
  font-size: 18px;
  color: #111827;
  margin-bottom: 6px;
}

.list-header span {
  font-size: 12px;
  color: #6b7280;
}

.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.book-card {
  border-radius: 14px;
  overflow: hidden;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.2s ease;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(17, 24, 39, 0.08);
}

.cover img {
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
  color: #374151;
}

.pager {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 1200px) {
  .grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 960px) {
  .highlight {
    grid-template-columns: 1fr;
  }

  .content {
    grid-template-columns: 1fr;
  }

  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .search-bar {
    flex-direction: column;
  }

  .mini-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
