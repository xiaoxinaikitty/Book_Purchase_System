<template>
  <div class="order-detail" v-loading="loading">
    <header class="detail-header">
      <el-button text @click="goBack">← 返回订单列表</el-button>
    </header>

    <div v-if="order" class="detail-card">
      <div class="summary">
        <h1>订单详情</h1>
        <div class="summary-row">
          <span>订单号：</span>
          <strong>{{ order.orderNo }}</strong>
        </div>
        <div class="summary-row">
          <span>状态：</span>
          <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
        </div>
        <div class="summary-row">
          <span>收货人：</span>
          <strong>{{ order.receiver }}</strong>
        </div>
        <div class="summary-row">
          <span>联系电话：</span>
          <strong>{{ order.phone }}</strong>
        </div>
        <div class="summary-row">
          <span>收货地址：</span>
          <strong>{{ order.address }}</strong>
        </div>
        <div class="summary-row">
          <span>备注：</span>
          <strong>{{ order.remark || '无' }}</strong>
        </div>
      </div>

      <div class="items">
        <h2>商品列表</h2>
        <div v-for="item in order.orderItems || []" :key="item.id" class="item">
          <img :src="item.bookCover || defaultCover" alt="cover" />
          <div class="info">
            <div class="title">{{ item.bookTitle }}</div>
            <div class="meta">数量 x {{ item.quantity }}</div>
          </div>
          <div class="price">¥{{ formatPrice(item.subtotal) }}</div>
        </div>
      </div>
    </div>

    <div v-if="order" class="total-card">
      <div>订单总额</div>
      <div class="amount">¥{{ formatPrice(order.totalAmount) }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail } from '@/api/order'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const loading = ref(false)
const defaultCover = '/vite.svg'

const statusText = (val) => {
  const map = {
    0: '待付款',
    1: '已付款',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return map[val] || '未知'
}

const statusType = (val) => {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'success',
    4: 'danger'
  }
  return map[val] || 'info'
}

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch (error) {
    console.error('获取订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/orders')
}

onMounted(loadDetail)
watch(() => route.params.id, loadDetail)
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  background: #f6f8fb;
  padding: 24px;
}

.detail-header {
  max-width: 960px;
  margin: 0 auto 16px;
}

.detail-card {
  max-width: 960px;
  margin: 0 auto;
  background: #fff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.08);
  display: grid;
  gap: 24px;
}

.summary h1 {
  font-size: 22px;
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  gap: 10px;
  font-size: 14px;
  color: #4b5563;
  margin-bottom: 8px;
}

.items h2 {
  font-size: 18px;
  margin-bottom: 12px;
}

.item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
}

.item img {
  width: 64px;
  height: 90px;
  object-fit: cover;
  border-radius: 10px;
}

.item .title {
  font-weight: 600;
  color: #111827;
}

.item .meta {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.price {
  margin-left: auto;
  font-weight: 600;
  color: #ef4444;
}

.total-card {
  max-width: 960px;
  margin: 16px auto 0;
  background: #fff;
  border-radius: 16px;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  box-shadow: 0 8px 20px rgba(17, 24, 39, 0.06);
}

.amount {
  font-size: 20px;
  font-weight: 700;
  color: #ef4444;
}
</style>
