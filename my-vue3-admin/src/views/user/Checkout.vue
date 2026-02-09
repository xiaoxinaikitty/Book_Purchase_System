<template>
  <div class="checkout-page">
    <header class="page-header">
      <div>
        <h1>确认订单</h1>
        <p>核对收货地址与商品信息</p>
      </div>
      <el-button text @click="goBack">← 返回购物车</el-button>
    </header>

    <div class="checkout-grid">
      <section class="card">
        <div class="section-header">
          <h3>选择收货地址</h3>
          <el-button size="small" type="primary" @click="openAddress">新增地址</el-button>
        </div>
        <el-radio-group v-model="selectedAddressId" class="address-list">
          <el-radio
            v-for="item in addressList"
            :key="item.id"
            :label="item.id"
          >
            {{ item.receiver }} {{ item.phone }}
            <div class="address-detail">
              {{ item.province }}{{ item.city }}{{ item.district }}{{ item.detail }}
            </div>
          </el-radio>
        </el-radio-group>
      </section>

      <section class="card">
        <div class="section-header">
          <h3>商品清单</h3>
        </div>
        <div v-for="item in items" :key="item.id" class="item-row">
          <img :src="item.book?.coverImage || defaultCover" alt="cover" />
          <div class="info">
            <div class="title">{{ item.book?.title }}</div>
            <div class="meta">x{{ item.quantity }}</div>
          </div>
          <div class="price">¥{{ formatPrice((item.book?.price || 0) * item.quantity) }}</div>
        </div>
        <div class="total">
          合计：¥{{ formatPrice(totalAmount) }}
        </div>
        <el-button type="primary" size="large" :loading="submitting" @click="submitOrder">
          提交订单
        </el-button>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCartList } from '@/api/cart'
import { getAddressList } from '@/api/address'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()

const items = ref([])
const addressList = ref([])
const selectedAddressId = ref(null)
const submitting = ref(false)

const defaultCover = '/vite.svg'

const totalAmount = computed(() => {
  return items.value.reduce((sum, item) => {
    return sum + (item.book?.price || 0) * item.quantity
  }, 0)
})

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadCartItems = async () => {
  const ids = (route.query.ids || '').toString().split(',').filter(Boolean)
  if (ids.length === 0) {
    items.value = []
    return
  }
  const res = await getCartList()
  const all = res.data || []
  items.value = all.filter((item) => ids.includes(String(item.id)))
}

const loadAddress = async () => {
  const res = await getAddressList()
  addressList.value = res.data || []
  const defaultItem = addressList.value.find((item) => item.isDefault === 1)
  selectedAddressId.value = defaultItem ? defaultItem.id : addressList.value[0]?.id || null
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (items.value.length === 0) {
    ElMessage.warning('没有可结算的商品')
    return
  }
  submitting.value = true
  try {
    const cartIds = items.value.map((item) => item.id)
    const res = await createOrder({ cartIds, addressId: selectedAddressId.value })
    ElMessage.success(res.message || '创建成功')
    router.push('/orders')
  } catch (error) {
    console.error('提交订单失败:', error)
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/cart')
}

const openAddress = () => {
  router.push('/address')
}

onMounted(() => {
  loadCartItems()
  loadAddress()
})
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.checkout-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(17, 24, 39, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.address-list {
  display: grid;
  gap: 10px;
}

.address-detail {
  font-size: 12px;
  color: #6b7280;
  margin-left: 22px;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}

.item-row img {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.info .title {
  font-weight: 600;
}

.info .meta {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.price {
  margin-left: auto;
  font-weight: 600;
  color: #ef4444;
}

.total {
  font-size: 16px;
  font-weight: 600;
  margin-top: 12px;
  margin-bottom: 12px;
}

@media (max-width: 960px) {
  .checkout-grid {
    grid-template-columns: 1fr;
  }
}
</style>
