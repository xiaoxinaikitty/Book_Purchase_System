<template>
  <div class="books-page">
    <section class="books-hero">
      <div class="hero-copy">
        <el-button text class="back-btn" @click="goBack">← 返回首页</el-button>
        <div class="hero-kicker">Book Plaza</div>
        <h1>把阅读变成一场温柔的漫游</h1>
        <p>
          从热销榜、新书架和多元分类中找到今天的阅读灵感，让图书广场像一家被阳光照亮的书店一样好逛。
        </p>

        <div class="search-panel">
          <el-input
            v-model="keyword"
            placeholder="搜索书名 / 作者 / 出版社"
            size="large"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" size="large" @click="handleSearch">搜索图书</el-button>
          <el-button plain size="large" @click="handleReset">重置条件</el-button>
        </div>

        <div class="hero-badges">
          <div class="hero-badge">
            <strong>{{ total }}</strong>
            <span>当前可浏览图书</span>
          </div>
          <div class="hero-badge">
            <strong>{{ categories.length }}</strong>
            <span>精选分类主题</span>
          </div>
          <div class="hero-badge">
            <strong>{{ hotBooks.length + newBooks.length }}</strong>
            <span>热销与新书灵感</span>
          </div>
        </div>
      </div>

      <div class="hero-side">
        <div class="mood-card">
          <div class="mood-label">今日阅读地图</div>
          <h3>{{ activeCategoryLabel }}</h3>
          <p>
            {{ keyword ? `正在为你搜索“${keyword}”相关图书` : '从全部馆藏中慢慢挑一本适合此刻心情的书' }}
          </p>

          <div class="mood-tags">
            <span v-for="tag in showcaseTags" :key="tag" class="mood-tag">{{ tag }}</span>
          </div>

          <div class="mood-stats">
            <div>
              <strong>{{ hotBooks.length }}</strong>
              <span>热榜精选</span>
            </div>
            <div>
              <strong>{{ newBooks.length }}</strong>
              <span>新书速递</span>
            </div>
            <div>
              <strong>{{ page }}</strong>
              <span>当前页码</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="showcase-grid">
      <article class="showcase-card warm">
        <div class="showcase-header">
          <div>
            <span class="showcase-kicker">热读焦点</span>
            <h2>热门图书榜</h2>
            <p>基于销量热度整理，适合快速了解近期最受欢迎的书。</p>
          </div>
        </div>

        <div v-if="hotLead" class="showcase-body">
          <div class="spotlight-book" @click="goDetail(hotLead.id)">
            <img :src="hotLead.coverImage || defaultCover" alt="cover" />
            <div class="spotlight-overlay">
              <span class="spotlight-badge">TOP 1</span>
              <h3>{{ hotLead.title }}</h3>
              <p>{{ hotLead.author || '未知作者' }}</p>
              <div class="spotlight-meta">
                <span>评分 {{ hotLead.rating || 0 }}</span>
                <span>¥{{ formatPrice(hotLead.price) }}</span>
              </div>
            </div>
          </div>

          <div class="spotlight-list">
            <div
              v-for="(book, index) in hotTrail"
              :key="book.id"
              class="list-item"
              @click="goDetail(book.id)"
            >
              <span class="rank">{{ index + 2 }}</span>
              <div class="list-copy">
                <strong>{{ book.title }}</strong>
                <span>{{ book.author || '未知作者' }}</span>
              </div>
              <em>¥{{ formatPrice(book.price) }}</em>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无热门图书" />
      </article>

      <article class="showcase-card fresh">
        <div class="showcase-header">
          <div>
            <span class="showcase-kicker">新鲜上架</span>
            <h2>新书速递</h2>
            <p>为你收集最近上架的图书，让书架总有新的惊喜。</p>
          </div>
        </div>

        <div v-if="newLead" class="showcase-body">
          <div class="spotlight-book soft" @click="goDetail(newLead.id)">
            <img :src="newLead.coverImage || defaultCover" alt="cover" />
            <div class="spotlight-overlay">
              <span class="spotlight-badge secondary">NEW</span>
              <h3>{{ newLead.title }}</h3>
              <p>{{ newLead.author || '未知作者' }}</p>
              <div class="spotlight-meta">
                <span>{{ newLead.categoryName || '最新上架' }}</span>
                <span>¥{{ formatPrice(newLead.price) }}</span>
              </div>
            </div>
          </div>

          <div class="spotlight-list">
            <div
              v-for="book in newTrail"
              :key="book.id"
              class="list-item light"
              @click="goDetail(book.id)"
            >
              <div class="list-copy">
                <strong>{{ book.title }}</strong>
                <span>{{ book.categoryName || book.author || '温暖阅读' }}</span>
              </div>
              <em>¥{{ formatPrice(book.price) }}</em>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无新书数据" />
      </article>
    </section>

    <section class="catalog-layout">
      <aside class="filter-rail">
        <div class="filter-card">
          <div class="rail-header">
            <div>
              <h3>分类筛选</h3>
              <p>按你的阅读兴趣缩小范围</p>
            </div>
            <span class="rail-count">{{ categories.length }} 类</span>
          </div>

          <div class="active-tip">
            <span>当前主题</span>
            <strong>{{ activeCategoryLabel }}</strong>
          </div>

          <div class="category-cloud">
            <button
              class="category-chip"
              :class="{ active: activeCategory === null }"
              @click="selectCategory(null)"
            >
              全部分类
            </button>
            <button
              v-for="item in categories"
              :key="item.id"
              class="category-chip"
              :class="{ active: activeCategory === item.id }"
              @click="selectCategory(item.id)"
            >
              {{ item.name }}
            </button>
          </div>
        </div>

        <div class="aside-card">
          <div class="aside-kicker">阅读提示</div>
          <h3>让找书过程更轻松</h3>
          <ul class="aside-list">
            <li>先看热榜，快速感知最近高人气图书。</li>
            <li>结合分类筛选与关键词，结果会更精准。</li>
            <li>卡片中会优先展示评分、价格和分类信息。</li>
          </ul>
        </div>
      </aside>

      <section class="catalog-stage">
        <div class="catalog-head">
          <div>
            <span class="catalog-kicker">图书目录</span>
            <h2>为你整理的温馨书架</h2>
            <p>
              {{ keyword ? `搜索结果共 ${total} 本` : `当前展示全部书库中的 ${total} 本图书` }}
            </p>
          </div>
          <div class="catalog-meta">
            <span class="soft-pill">{{ activeCategoryLabel }}</span>
            <span class="soft-pill">{{ keyword || '未输入关键词' }}</span>
          </div>
        </div>

        <el-skeleton v-if="loading" animated :rows="8" />
        <el-empty v-else-if="books.length === 0" description="没有找到符合条件的图书" />

        <div v-else class="book-grid">
          <article
            v-for="book in books"
            :key="book.id"
            class="book-card"
            @click="goDetail(book.id)"
          >
            <div class="cover-shell">
              <img :src="book.coverImage || defaultCover" alt="cover" />
              <span class="floating-tag">{{ book.categoryName || '精选图书' }}</span>
            </div>

            <div class="book-body">
              <div class="book-topline">
                <span class="score-chip">评分 {{ book.rating || 0 }}</span>
                <strong class="book-price">¥{{ formatPrice(book.price) }}</strong>
              </div>

              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author || '未知作者' }}</p>
              <p class="book-desc">{{ book.publisher || '适合慢下来阅读的一本好书' }}</p>

              <div class="book-footer">
                <span>{{ book.categoryName || activeCategoryLabel }}</span>
                <em>查看详情</em>
              </div>
            </div>
          </article>
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
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
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

const hotLead = computed(() => hotBooks.value[0] || null)
const hotTrail = computed(() => hotBooks.value.slice(1, 5))
const newLead = computed(() => newBooks.value[0] || null)
const newTrail = computed(() => newBooks.value.slice(1, 5))

const activeCategoryLabel = computed(() => {
  const current = categories.value.find((item) => item.id === activeCategory.value)
  return current?.name || '全部分类'
})

const showcaseTags = computed(() => {
  const seeds = [
    activeCategoryLabel.value,
    keyword.value ? `关键词：${keyword.value}` : '慢享阅读',
    hotLead.value?.author,
    newLead.value?.categoryName,
    '温暖书单'
  ]

  return [...new Set(seeds.filter(Boolean))].slice(0, 4)
})

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
    const [hotRes, newRes] = await Promise.all([getHotBooks(6), getNewBooks(6)])
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

const handleReset = () => {
  keyword.value = ''
  activeCategory.value = null
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

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}

onMounted(() => {
  loadCategories()
  loadBooks()
  loadHighlights()
})
</script>

<style scoped>
.books-page {
  --peach: #fff1e6;
  --cream: #fffaf4;
  --rose: #f9d8c3;
  --amber: #e89a5f;
  --coffee: #715547;
  --forest: #769e89;
  --shadow: 0 24px 60px rgba(156, 116, 79, 0.14);
  display: grid;
  gap: 22px;
  padding-bottom: 28px;
}

.books-hero {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.9fr);
  gap: 20px;
  padding: 32px;
  border-radius: 30px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.55), transparent 28%),
    linear-gradient(135deg, #fff3df 0%, #ffe7d0 46%, #f7d7c4 100%);
  box-shadow: var(--shadow);
}

.books-hero::before,
.books-hero::after {
  content: '';
  position: absolute;
  border-radius: 999px;
  opacity: 0.7;
}

.books-hero::before {
  width: 240px;
  height: 240px;
  top: -110px;
  right: -50px;
  background: rgba(255, 255, 255, 0.45);
}

.books-hero::after {
  width: 180px;
  height: 180px;
  bottom: -70px;
  right: 180px;
  background: rgba(255, 222, 173, 0.55);
}

.hero-copy,
.hero-side {
  position: relative;
  z-index: 1;
}

.back-btn {
  padding-left: 0;
  color: #8f5d3b;
  margin-bottom: 10px;
}

.back-btn:hover {
  color: #6a4329;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.68);
  color: #a45d34;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-copy h1 {
  margin: 16px 0 12px;
  font-size: 40px;
  line-height: 1.12;
  color: #5c3828;
}

.hero-copy p {
  max-width: 720px;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(92, 56, 40, 0.82);
}

.search-panel {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  gap: 12px;
  margin-top: 24px;
  padding: 14px;
  border: 1px solid rgba(225, 180, 147, 0.45);
  border-radius: 20px;
  background: rgba(255, 250, 244, 0.78);
  backdrop-filter: blur(10px);
}

.hero-badges {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 22px;
}

.hero-badge {
  padding: 18px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(232, 183, 147, 0.4);
}

.hero-badge strong {
  display: block;
  font-size: 28px;
  color: #5c3828;
}

.hero-badge span {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: #8a6754;
}

.hero-side {
  display: flex;
  align-items: stretch;
}

.mood-card {
  width: 100%;
  padding: 24px;
  border-radius: 26px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(255, 247, 239, 0.92));
  border: 1px solid rgba(232, 183, 147, 0.42);
  box-shadow: 0 18px 36px rgba(173, 129, 92, 0.12);
}

.mood-label {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--peach);
  color: var(--amber);
  font-size: 12px;
  font-weight: 700;
}

.mood-card h3 {
  margin-top: 16px;
  font-size: 28px;
  color: var(--coffee);
}

.mood-card p {
  margin-top: 10px;
  line-height: 1.75;
  color: #82685a;
}

.mood-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.mood-tag {
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(118, 158, 137, 0.14);
  color: #5b7f6b;
  font-size: 12px;
}

.mood-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 22px;
}

.mood-stats div {
  padding: 14px 12px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.78);
  text-align: center;
}

.mood-stats strong {
  display: block;
  font-size: 22px;
  color: var(--coffee);
}

.mood-stats span {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #907466;
}

.showcase-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.showcase-card {
  padding: 24px;
  border-radius: 26px;
  background: var(--cream);
  border: 1px solid rgba(234, 222, 211, 0.88);
  box-shadow: 0 18px 42px rgba(126, 94, 66, 0.08);
}

.showcase-card.fresh {
  background: linear-gradient(180deg, #fffaf2, #fff5ee);
}

.showcase-kicker {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(232, 154, 95, 0.14);
  color: var(--amber);
  font-size: 12px;
  font-weight: 700;
}

.showcase-header h2 {
  margin: 14px 0 8px;
  font-size: 24px;
  color: var(--coffee);
}

.showcase-header p {
  color: #8d7466;
  line-height: 1.7;
}

.showcase-body {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(220px, 0.9fr);
  gap: 16px;
  margin-top: 20px;
}

.spotlight-book {
  position: relative;
  min-height: 320px;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  background: #ead6c7;
}

.spotlight-book.soft {
  background: #f1e0d0;
}

.spotlight-book img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spotlight-overlay {
  position: absolute;
  inset: auto 0 0;
  padding: 18px;
  background: linear-gradient(180deg, transparent, rgba(46, 29, 20, 0.84));
  color: #fff8f2;
}

.spotlight-badge {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  font-size: 12px;
  font-weight: 700;
}

.spotlight-badge.secondary {
  background: rgba(255, 238, 204, 0.24);
}

.spotlight-overlay h3 {
  margin-top: 12px;
  font-size: 24px;
}

.spotlight-overlay p {
  margin-top: 8px;
  color: rgba(255, 248, 242, 0.84);
}

.spotlight-meta {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-top: 14px;
  font-size: 13px;
}

.spotlight-list {
  display: grid;
  gap: 12px;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border-radius: 18px;
  background: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.list-item.light {
  background: #fffaf6;
}

.list-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(150, 108, 77, 0.1);
}

.rank {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #efb37d, #df8f55);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  flex-shrink: 0;
}

.list-copy {
  flex: 1;
  min-width: 0;
  display: grid;
  gap: 4px;
}

.list-copy strong {
  color: var(--coffee);
  font-size: 14px;
}

.list-copy span {
  color: #9a7f70;
  font-size: 12px;
}

.list-item em {
  color: var(--amber);
  font-style: normal;
  font-weight: 700;
}

.catalog-layout {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 18px;
}

.filter-rail {
  display: grid;
  gap: 16px;
  align-content: start;
}

.filter-card,
.aside-card,
.catalog-stage {
  border-radius: 26px;
  background: rgba(255, 253, 249, 0.96);
  border: 1px solid rgba(234, 222, 211, 0.88);
  box-shadow: 0 18px 40px rgba(126, 94, 66, 0.08);
}

.filter-card,
.aside-card {
  padding: 22px;
}

.filter-card {
  position: sticky;
  top: 84px;
}

.rail-header {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: flex-start;
}

.rail-header h3,
.aside-card h3 {
  font-size: 20px;
  color: var(--coffee);
}

.rail-header p {
  margin-top: 6px;
  font-size: 13px;
  color: #92796c;
}

.rail-count {
  padding: 7px 10px;
  border-radius: 999px;
  background: var(--peach);
  color: var(--amber);
  font-size: 12px;
  font-weight: 700;
}

.active-tip {
  margin-top: 20px;
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, #fff4e9, #fffaf4);
}

.active-tip span {
  display: block;
  font-size: 12px;
  color: #a18472;
}

.active-tip strong {
  display: block;
  margin-top: 6px;
  font-size: 18px;
  color: var(--coffee);
}

.category-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.category-chip {
  border: none;
  border-radius: 999px;
  padding: 10px 14px;
  background: #fff3ea;
  color: #8b674f;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-chip:hover,
.category-chip.active {
  background: linear-gradient(135deg, #ec9a63, #d97745);
  color: #fff;
}

.aside-card {
  background: linear-gradient(180deg, #fffdf9, #fff6ee);
}

.aside-kicker,
.catalog-kicker {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(118, 158, 137, 0.14);
  color: #628b76;
  font-size: 12px;
  font-weight: 700;
}

.aside-list {
  margin: 18px 0 0;
  padding-left: 18px;
  color: #8d7466;
  line-height: 1.8;
}

.catalog-stage {
  padding: 22px;
}

.catalog-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.catalog-head h2 {
  margin: 14px 0 8px;
  font-size: 28px;
  color: var(--coffee);
}

.catalog-head p {
  color: #907466;
}

.catalog-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.soft-pill {
  padding: 8px 12px;
  border-radius: 999px;
  background: #fff2e7;
  color: #9d6847;
  font-size: 12px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.book-card {
  overflow: hidden;
  border-radius: 22px;
  background: #fff;
  border: 1px solid rgba(239, 225, 212, 0.92);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 18px 34px rgba(156, 116, 79, 0.14);
}

.cover-shell {
  position: relative;
  padding: 14px 14px 0;
}

.cover-shell img {
  width: 100%;
  height: 220px;
  border-radius: 18px;
  object-fit: cover;
  background: #f2e4d8;
}

.floating-tag {
  position: absolute;
  left: 26px;
  bottom: 14px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 250, 244, 0.92);
  color: #936648;
  font-size: 12px;
  box-shadow: 0 10px 20px rgba(128, 92, 64, 0.12);
}

.book-body {
  padding: 16px 16px 18px;
}

.book-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.score-chip {
  padding: 6px 10px;
  border-radius: 999px;
  background: #eef6f1;
  color: #5d856f;
  font-size: 12px;
}

.book-price {
  color: var(--amber);
  font-size: 22px;
}

.book-title {
  margin-top: 14px;
  font-size: 18px;
  line-height: 1.45;
  color: var(--coffee);
}

.book-author {
  margin-top: 8px;
  color: #8f7263;
}

.book-desc {
  margin-top: 12px;
  min-height: 44px;
  color: #9b8375;
  line-height: 1.6;
  font-size: 13px;
}

.book-footer {
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px dashed #ecdccf;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  color: #8f7263;
  font-size: 12px;
}

.book-footer em {
  font-style: normal;
  color: #db7b46;
  font-weight: 700;
}

.pager {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 1280px) {
  .books-hero {
    grid-template-columns: minmax(0, 1fr);
  }

  .catalog-layout {
    grid-template-columns: minmax(0, 1fr);
  }

  .filter-card {
    position: static;
  }

  .book-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .showcase-grid,
  .showcase-body,
  .hero-badges {
    grid-template-columns: minmax(0, 1fr);
  }

  .catalog-head {
    flex-direction: column;
  }
}

@media (max-width: 720px) {
  .books-hero,
  .showcase-card,
  .catalog-stage,
  .filter-card,
  .aside-card {
    padding: 18px;
  }

  .hero-copy h1 {
    font-size: 30px;
  }

  .search-panel {
    grid-template-columns: minmax(0, 1fr);
  }

  .mood-stats {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .book-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 560px) {
  .mood-stats {
    grid-template-columns: minmax(0, 1fr);
  }

  .catalog-meta {
    width: 100%;
  }

  .soft-pill {
    width: 100%;
    text-align: center;
  }
}
</style>
