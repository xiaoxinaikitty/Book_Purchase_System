<template>
  <div class="history-page">
    <header class="page-header">
      <div>
        <h1>浏览记录</h1>
        <p>回顾最近浏览过的图书</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" plain @click="router.push('/books')">继续逛书</el-button>
        <el-button type="danger" plain @click="handleClear">清空记录</el-button>
      </div>
    </header>

    <el-card class="list-card" shadow="never">
      <div v-loading="loading" class="list-body">
        <el-empty v-if="!loading && list.length === 0" description="暂无浏览记录" />
        <div v-else class="grid">
          <div v-for="item in list" :key="item.id" class="history-card">
            <div class="cover" @click="goDetail(item)">
              <img :src="item.book?.coverImage || defaultCover" alt="cover" />
            </div>
            <div class="info">
              <h3 class="title" @click="goDetail(item)">{{ item.book?.title || '未知图书' }}</h3>
              <p class="author">{{ item.book?.author || '未知作者' }}</p>
              <div class="meta">
                <span>浏览 {{ item.browseCount || 0 }} 次</span>
                <span>{{ formatDate(item.lastBrowseTime) }}</span>
              </div>
              <div class="price">¥{{ formatPrice(item.book?.price) }}</div>
              <div class="actions">
                <el-button size="small" type="primary" @click="addCart(item)">加入购物车</el-button>
                <el-button size="small" @click="goDetail(item)">查看详情</el-button>
                <el-button size="small" type="danger" plain @click="remove(item)">删除记录</el-button>
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
import { getHistoryList, clearHistory, deleteHistory } from '@/api/history'
import { addToCart } from '@/api/cart'

const router = useRouter()

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(false)
const defaultCover = '/vite.svg'

const formatDate = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ')
}

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHistoryList({
      page: page.value,
      size: size.value
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取浏览记录失败:', error)
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
  try {
    await ElMessageBox.confirm('确定删除该浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteHistory(item.id)
    ElMessage.success(res.message || '删除成功')
    if (list.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除浏览记录失败:', error)
    }
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定清空所有浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await clearHistory()
    ElMessage.success(res.message || '清空成功')
    page.value = 1
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空浏览记录失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.history-page {
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

.header-actions {
  display: flex;
  gap: 10px;
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

.history-card {
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

.price {
  font-size: 16px;
  font-weight: 700;
  color: #ef4444;
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
  .history-card {
    grid-template-columns: 1fr;
  }

  .cover img {
    height: 220px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
