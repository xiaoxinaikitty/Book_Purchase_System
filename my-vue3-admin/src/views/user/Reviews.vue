<template>
  <div class="reviews-page">
    <header class="page-header">
      <div>
        <h1>我的评价</h1>
        <p>查看你提交过的图书评价</p>
      </div>
      <el-button type="primary" plain @click="router.push('/books')">去选书</el-button>
    </header>

    <el-card class="review-card" shadow="never">
      <div v-loading="loading" class="review-list">
        <el-empty v-if="!loading && list.length === 0" description="还没有评价记录" />
        <div v-else class="review-items">
          <div v-for="item in list" :key="item.id" class="review-item">
            <div class="review-info">
              <div class="book-tag">图书ID：{{ item.bookId }}</div>
              <div class="rating-row">
                <el-rate :model-value="item.rating || 0" disabled />
                <span class="time">{{ formatDate(item.createTime) }}</span>
              </div>
              <p class="content">{{ item.content }}</p>
            </div>
            <div class="actions">
              <el-button type="primary" text @click="goBook(item.bookId)">查看图书</el-button>
              <el-button type="danger" text @click="removeReview(item)">删除</el-button>
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
          :page-sizes="[5, 10, 20]"
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
import { getMyReviews, deleteReview } from '@/api/review'

const router = useRouter()

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

const formatDate = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyReviews({
      page: page.value,
      size: size.value
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取评价列表失败:', error)
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

const goBook = (bookId) => {
  router.push(`/book/${bookId}`)
}

const removeReview = async (item) => {
  try {
    await ElMessageBox.confirm('确定删除该评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteReview(item.id)
    ElMessage.success(res.message || '删除成功')
    if (list.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评价失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.reviews-page {
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

.review-card {
  border-radius: 16px;
  border: none;
}

.review-list {
  min-height: 200px;
}

.review-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.06);
}

.review-info {
  flex: 1;
}

.book-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: #eef2ff;
  color: #4338ca;
  font-size: 12px;
  margin-bottom: 8px;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.time {
  font-size: 12px;
  color: #9ca3af;
}

.content {
  font-size: 13px;
  color: #4b5563;
  line-height: 1.7;
  margin: 0;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
  min-width: 100px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 720px) {
  .review-item {
    flex-direction: column;
  }

  .actions {
    flex-direction: row;
    justify-content: flex-start;
  }
}
</style>
