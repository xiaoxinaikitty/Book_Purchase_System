<template>
  <div class="favorites-page">
    <header class="page-header">
      <div>
        <h1>我的收藏</h1>
        <p>收藏的好书一键直达</p>
      </div>
      <el-button type="primary" plain @click="router.push('/books')">去逛书城</el-button>
    </header>

    <el-card class="list-card" shadow="never">
      <div v-loading="loading" class="list-body">
        <el-empty v-if="!loading && list.length === 0" description="还没有收藏记录" />
        <div v-else class="grid">
          <div v-for="item in list" :key="item.id" class="favorite-card">
            <div class="cover" @click="goDetail(item)">
              <img :src="item.book?.coverImage || defaultCover" alt="cover" />
            </div>
            <div class="info">
              <h3 class="title" @click="goDetail(item)">{{ item.book?.title || '未知图书' }}</h3>
              <p class="author">{{ item.book?.author || '未知作者' }}</p>
              <div class="meta">
                <span>评分 {{ item.book?.rating || 0 }}</span>
                <span>¥{{ formatPrice(item.book?.price) }}</span>
              </div>
              <div class="actions">
                <el-button size="small" type="primary" @click="addCart(item)">加入购物车</el-button>
                <el-button size="small" @click="goDetail(item)">查看详情</el-button>
                <el-button size="small" type="danger" plain @click="remove(item)">取消收藏</el-button>
              </div>
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
          :page-sizes="[6, 12, 24]"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavoriteList, removeFavorite } from '@/api/favorite'
import { addToCart } from '@/api/cart'

const router = useRouter()

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(false)
const defaultCover = '/vite.svg'

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList({
      page: page.value,
      size: size.value
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  page.value = val
  loadData()
}

const handleSizeChange = (val) => {
  size.value = val
  page.value = 1
  loadData()
}

const goDetail = (item) => {
  if (!item.book?.id) return
  router.push(`/book/${item.book.id}`)
}

const addCart = async (item) => {
  if (!item.book?.id) return
  try {
    const res = await addToCart({ bookId: item.book.id, quantity: 1 })
    ElMessage.success(res.message || '已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

const remove = async (item) => {
  if (!item.bookId) return
  try {
    await ElMessageBox.confirm('确定取消收藏该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await removeFavorite(item.bookId)
    ElMessage.success(res.message || '取消收藏成功')
    if (list.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 6px;
}

.page-header p {
  color: #6b7280;
  font-size: 13px;
}

.list-card {
  border-radius: 16px;
  border: none;
}

.list-body {
  min-height: 200px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.favorite-card {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.08);
}

.cover img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 12px;
  cursor: pointer;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  cursor: pointer;
}

.author {
  font-size: 13px;
  color: #6b7280;
}

.meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #4b5563;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 6px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .favorite-card {
    grid-template-columns: 1fr;
  }

  .cover img {
    height: 220px;
  }
}
</style>
