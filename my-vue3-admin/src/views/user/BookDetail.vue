<template>
  <div class="detail-page" v-loading="loading">
    <header class="detail-header">
      <el-button text @click="goBack">← 返回列表</el-button>
    </header>

    <div v-if="book" class="detail-card">
      <div class="cover">
        <img :src="book.coverImage || defaultCover" alt="cover" />
      </div>
      <div class="info">
        <h1>{{ book.title }}</h1>
        <p class="author">{{ book.author || '未知作者' }}</p>
        <div class="meta">
          <span>出版社：{{ book.publisher || '暂无' }}</span>
          <span>ISBN：{{ book.isbn || '暂无' }}</span>
          <span>分类：{{ book.categoryName || '未分类' }}</span>
        </div>
        <div class="price">¥{{ formatPrice(book.price) }}</div>
        <div class="stats">
          <el-tag type="success">评分 {{ book.rating || 0 }}</el-tag>
          <el-tag type="info">销量 {{ book.sales || 0 }}</el-tag>
          <el-tag :type="book.stock > 0 ? 'warning' : 'danger'">
            库存 {{ book.stock || 0 }}
          </el-tag>
        </div>
        <el-button type="primary" size="large" disabled>加入购物车（待开发）</el-button>
      </div>
    </div>

    <section v-if="book" class="desc-card">
      <h2>图书简介</h2>
      <p>{{ book.description || '暂无简介' }}</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBookDetail } from '@/api/book'

const route = useRoute()
const router = useRouter()

const book = ref(null)
const loading = ref(false)
const defaultCover = '/vite.svg'

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
  } catch (error) {
    console.error('获取图书详情失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/books')
}

onMounted(loadDetail)
watch(() => route.params.id, loadDetail)
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f6f8fb;
  padding: 24px;
}

.detail-header {
  max-width: 1200px;
  margin: 0 auto 16px;
}

.detail-card {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
}

.cover img {
  width: 100%;
  height: 360px;
  object-fit: cover;
  border-radius: 16px;
}

.info h1 {
  font-size: 26px;
  color: #111827;
  margin-bottom: 10px;
}

.author {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 16px;
}

.meta {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #4b5563;
  margin-bottom: 16px;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
  margin-bottom: 16px;
}

.stats {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
}

.desc-card {
  max-width: 1200px;
  margin: 24px auto 0;
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.06);
}

.desc-card h2 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #111827;
}

.desc-card p {
  color: #4b5563;
  line-height: 1.7;
}

@media (max-width: 900px) {
  .detail-card {
    grid-template-columns: 1fr;
  }

  .cover img {
    height: 300px;
  }
}
</style>
