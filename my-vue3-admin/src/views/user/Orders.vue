<template>
  <div class="orders-page">
    <header class="page-header">
      <div>
        <h1>我的订单</h1>
        <p>查看订单进度与状态</p>
      </div>
      <el-select v-model="status" placeholder="订单状态" clearable @change="handleFilter">
        <el-option label="待付款" :value="0" />
        <el-option label="已付款" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
    </header>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">¥{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="statusType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" min-width="240">
          <template #default="{ row }">
            <el-button size="small" @click="goDetail(row.id)">详情</el-button>
            <el-button
              v-if="row.status === 0"
              size="small"
              type="primary"
              @click="pay(row)"
            >
              支付
            </el-button>
            <el-button
              v-if="row.status === 0"
              size="small"
              type="warning"
              @click="cancel(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === 2"
              size="small"
              type="success"
              @click="confirm(row)"
            >
              确认收货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :page-sizes="[10, 20, 50]"
          :total="total"
          :page-size="size"
          :current-page="page"
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
import { getOrderList, cancelOrder, payOrder, confirmOrder } from '@/api/order'

const router = useRouter()

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const status = ref()
const loading = ref(false)

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

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      page: page.value,
      size: size.value,
      status: status.value
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  page.value = 1
  loadData()
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

const goDetail = (id) => {
  router.push(`/order/${id}`)
}

const pay = async (row) => {
  try {
    await ElMessageBox.confirm('确认支付该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await payOrder(row.id)
    ElMessage.success(res.message || '支付成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
    }
  }
}

const cancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await cancelOrder(row.id)
    ElMessage.success(res.message || '取消成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
    }
  }
}

const confirm = async (row) => {
  try {
    await ElMessageBox.confirm('确认已收到图书？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await confirmOrder(row.id)
    ElMessage.success(res.message || '确认收货成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.orders-page {
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

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
