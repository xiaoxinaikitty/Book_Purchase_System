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
        <div class="cart-action">
          <el-input-number v-model="quantity" :min="1" :max="book.stock || 999" />
          <el-button type="primary" size="large" @click="handleAddToCart">加入购物车</el-button>
          <el-button
            :type="isFavorited ? 'warning' : 'info'"
            size="large"
            :loading="favoriteLoading"
            @click="toggleFavorite"
          >
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </div>

    <section v-if="book" class="desc-card">
      <h2>图书简介</h2>
      <p>{{ book.description || '暂无简介' }}</p>
    </section>

    <section v-if="book" class="review-section">
      <div class="review-header">
        <div>
          <h2>用户评价</h2>
          <p>阅读体验来自真实购书用户</p>
        </div>
        <div class="review-summary">
          <el-rate :model-value="Number(book.rating || 0)" disabled show-score />
          <span>{{ reviewTotal }} 条评价</span>
        </div>
      </div>

      <div class="review-grid">
        <el-card class="review-form" shadow="never">
          <div class="form-title">
            <h3>写评价</h3>
            <span>每本图书仅可评价一次</span>
          </div>
          <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="70px">
            <el-form-item label="评分" prop="rating">
              <el-rate v-model="reviewForm.rating" />
            </el-form-item>
            <el-form-item label="评价" prop="content">
              <el-input
                v-model="reviewForm.content"
                type="textarea"
                :rows="4"
                maxlength="200"
                show-word-limit
                placeholder="分享你的阅读感受..."
              />
            </el-form-item>
            <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">
              提交评价
            </el-button>
          </el-form>
        </el-card>

        <el-card class="review-list" shadow="never">
          <div class="list-title">
            <h3>评价列表</h3>
            <span>按时间排序</span>
          </div>

          <div v-loading="reviewLoading" class="review-items">
            <div v-if="reviews.length === 0" class="review-empty">
              暂无评价，快来成为第一个评价的人吧～
            </div>
            <div v-else v-for="item in reviews" :key="item.id" class="review-item">
              <div class="review-avatar">
                <el-avatar :size="44" :src="item.avatar || undefined">
                  {{ item.nickname?.charAt(0) || 'U' }}
                </el-avatar>
              </div>
              <div class="review-body">
                <div class="review-head">
                  <div class="review-user">
                    <span class="name">{{ item.nickname || '匿名用户' }}</span>
                    <el-rate :model-value="item.rating || 0" disabled />
                  </div>
                  <div class="review-meta">
                    <span>{{ formatDate(item.createTime) }}</span>
                    <el-button
                      v-if="isMyReview(item)"
                      type="danger"
                      text
                      @click="handleDeleteReview(item)"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
                <p class="review-content">{{ item.content }}</p>
              </div>
            </div>
          </div>

          <div class="review-pager">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="reviewTotal"
              :page-size="reviewSize"
              :current-page="reviewPage"
              @current-change="handleReviewPageChange"
            />
          </div>
        </el-card>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookDetail } from '@/api/book'
import { addToCart } from '@/api/cart'
import { addReview, getBookReviews, deleteReview } from '@/api/review'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const book = ref(null)
const loading = ref(false)
const defaultCover = '/vite.svg'
const quantity = ref(1)

const reviews = ref([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const reviewSize = ref(5)
const reviewLoading = ref(false)
const reviewSubmitting = ref(false)
const reviewFormRef = ref(null)

const isFavorited = ref(false)
const favoriteLoading = ref(false)

const reviewForm = reactive({
  rating: 5,
  content: ''
})

const reviewRules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, max: 200, message: '评价内容为5-200个字符', trigger: 'blur' }
  ]
}

const currentUserId = computed(() => userStore.userInfo.userId || userStore.userInfo.id)

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const formatDate = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ')
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
    quantity.value = 1
    await loadFavoriteStatus()
  } catch (error) {
    console.error('获取图书详情失败:', error)
  } finally {
    loading.value = false
  }
}

const loadReviews = async () => {
  if (!route.params.id) return
  reviewLoading.value = true
  try {
    const res = await getBookReviews(route.params.id, {
      page: reviewPage.value,
      size: reviewSize.value
    })
    reviews.value = res.data.records || []
    reviewTotal.value = res.data.total || 0
  } catch (error) {
    console.error('获取评价列表失败:', error)
  } finally {
    reviewLoading.value = false
  }
}

const loadFavoriteStatus = async () => {
  if (!route.params.id) return
  try {
    const res = await checkFavorite(route.params.id)
    isFavorited.value = !!res.data
  } catch (error) {
    console.error('获取收藏状态失败:', error)
  }
}

const goBack = () => {
  router.push('/books')
}

const handleAddToCart = async () => {
  if (!book.value) return
  try {
    const res = await addToCart({ bookId: book.value.id, quantity: quantity.value })
    ElMessage.success(res.message || '已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

const toggleFavorite = async () => {
  if (!book.value) return
  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      const res = await removeFavorite(book.value.id)
      ElMessage.success(res.message || '取消收藏成功')
      isFavorited.value = false
    } else {
      const res = await addFavorite(book.value.id)
      ElMessage.success(res.message || '收藏成功')
      isFavorited.value = true
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
  } finally {
    favoriteLoading.value = false
  }
}

const resetReviewForm = () => {
  reviewForm.rating = 5
  reviewForm.content = ''
  if (reviewFormRef.value) {
    reviewFormRef.value.clearValidate()
  }
}

const submitReview = async () => {
  if (!reviewFormRef.value || !book.value) return
  await reviewFormRef.value.validate(async (valid) => {
    if (!valid) return
    reviewSubmitting.value = true
    try {
      const res = await addReview({
        bookId: book.value.id,
        rating: reviewForm.rating,
        content: reviewForm.content
      })
      ElMessage.success(res.message || '评价成功')
      resetReviewForm()
      reviewPage.value = 1
      await loadDetail()
      await loadReviews()
    } catch (error) {
      console.error('提交评价失败:', error)
    } finally {
      reviewSubmitting.value = false
    }
  })
}

const handleReviewPageChange = (val) => {
  reviewPage.value = val
  loadReviews()
}

const isMyReview = (item) => {
  if (!currentUserId.value) return false
  return Number(item.userId) === Number(currentUserId.value)
}

const handleDeleteReview = async (item) => {
  try {
    await ElMessageBox.confirm('确定删除该评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteReview(item.id)
    ElMessage.success(res.message || '删除成功')
    await loadDetail()
    if (reviews.value.length === 1 && reviewPage.value > 1) {
      reviewPage.value -= 1
    }
    await loadReviews()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评价失败:', error)
    }
  }
}

const reloadAll = () => {
  loadDetail()
  reviewPage.value = 1
  resetReviewForm()
  loadReviews()
}

onMounted(() => {
  loadDetail()
  loadReviews()
})
watch(() => route.params.id, reloadAll)
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

.cart-action {
  display: flex;
  align-items: center;
  gap: 12px;
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

.review-section {
  max-width: 1200px;
  margin: 24px auto 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.review-header h2 {
  font-size: 18px;
  color: #111827;
  margin-bottom: 6px;
}

.review-header p {
  font-size: 12px;
  color: #6b7280;
}

.review-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #6b7280;
  font-size: 12px;
}

.review-grid {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 16px;
}

.review-form,
.review-list {
  border-radius: 16px;
  border: none;
  background: #fff;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.06);
}

.form-title,
.list-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-title h3,
.list-title h3 {
  font-size: 16px;
  color: #111827;
}

.form-title span,
.list-title span {
  font-size: 12px;
  color: #9ca3af;
}

.review-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 120px;
}

.review-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  background: #f9fafb;
}

.review-empty {
  padding: 32px 12px;
  text-align: center;
  color: #9ca3af;
  font-size: 13px;
}

.review-body {
  flex: 1;
}

.review-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.review-user {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.review-user .name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #9ca3af;
}

.review-content {
  font-size: 13px;
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
}

.review-pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 900px) {
  .detail-card {
    grid-template-columns: 1fr;
  }

  .cover img {
    height: 300px;
  }

  .review-grid {
    grid-template-columns: 1fr;
  }
}
</style>
