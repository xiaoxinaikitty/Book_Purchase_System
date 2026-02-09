<template>
  <div class="cart-page">
    <header class="page-header">
      <div>
        <h1>我的购物车</h1>
        <p>管理想购买的图书清单</p>
      </div>
      <el-button type="danger" plain @click="handleClear" :disabled="list.length === 0">
        清空购物车
      </el-button>
    </header>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="图书" min-width="260">
          <template #default="{ row }">
            <div class="book-info">
              <img :src="row.book?.coverImage || defaultCover" alt="cover" />
              <div>
                <div class="title">{{ row.book?.title || '未知图书' }}</div>
                <div class="author">{{ row.book?.author || '未知作者' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ formatPrice(row.book?.price) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="1"
              :max="row.book?.stock || 999"
              @change="(val) => handleQuantityChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ formatPrice((row.book?.price || 0) * row.quantity) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleRemove(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="footer">
        <div class="summary">
          <span>合计：</span>
          <strong>¥{{ formatPrice(totalAmount) }}</strong>
        </div>
        <el-button
          type="primary"
          size="large"
          :disabled="selectedIds.length === 0"
          @click="goCheckout"
        >
          去结算
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCartList,
  updateCartQuantity,
  removeFromCart,
  clearCart
} from '@/api/cart'

const router = useRouter()
const list = ref([])
const selectedIds = ref([])
const loading = ref(false)
const defaultCover = '/vite.svg'

const totalAmount = computed(() => {
  const source = selectedIds.value.length
    ? list.value.filter((item) => selectedIds.value.includes(item.id))
    : list.value
  return source.reduce((sum, item) => {
    const price = item.book?.price || 0
    return sum + price * item.quantity
  }, 0)
})

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    list.value = res.data || []
  } catch (error) {
    console.error('获取购物车失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuantityChange = async (row, value) => {
  try {
    const res = await updateCartQuantity(row.id, value)
    ElMessage.success(res.message || '更新成功')
  } catch (error) {
    console.error('更新数量失败:', error)
    loadData()
  }
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map((row) => row.id)
}

const goCheckout = () => {
  if (selectedIds.value.length === 0) return
  const ids = selectedIds.value.join(',')
  router.push(`/checkout?ids=${ids}`)
}

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await removeFromCart(row.id)
    ElMessage.success(res.message || '删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await clearCart()
    ElMessage.success(res.message || '清空成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.cart-page {
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

.table-card {
  border-radius: 14px;
}

.book-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.book-info img {
  width: 56px;
  height: 78px;
  object-fit: cover;
  border-radius: 8px;
}

.title {
  font-weight: 600;
  color: #111827;
}

.author {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.summary {
  font-size: 16px;
  color: #111827;
}
</style>
