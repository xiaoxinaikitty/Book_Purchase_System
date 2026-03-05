<template>
  <section class="admin-page">
    <PageHeader title="订单管理" description="追踪订单状态、发货进度和订单明细">
      <template #actions>
        <div class="admin-filter-bar">
          <el-input
            v-model="orderNo"
            placeholder="订单号搜索"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="status" placeholder="状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="已付款" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
          <el-button type="primary" @click="handleSearch">筛选</el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="170" />
        <el-table-column prop="username" label="用户" width="120" />
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
        <el-table-column label="操作" min-width="260">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" type="primary" @click="ship(row)">
              发货
            </el-button>
            <el-button size="small" type="danger" @click="removeOrder(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="admin-table-footer">
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
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="760px">
      <div v-if="detail" class="detail-wrap">
        <div class="detail-info">
          <div><span>订单号：</span>{{ detail.orderNo }}</div>
          <div><span>用户：</span>{{ detail.username }}</div>
          <div><span>收货人：</span>{{ detail.receiver }}</div>
          <div><span>电话：</span>{{ detail.phone }}</div>
          <div><span>地址：</span>{{ detail.address }}</div>
          <div><span>状态：</span>{{ statusText(detail.status) }}</div>
        </div>
        <el-divider />
        <div class="detail-items">
          <div v-for="item in detail.orderItems || []" :key="item.id" class="item">
            <img :src="item.bookCover || defaultCover" alt="cover" />
            <div class="title">{{ item.bookTitle }}</div>
            <div class="meta">x{{ item.quantity }}</div>
            <div class="price">¥{{ formatPrice(item.subtotal) }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderList, getAdminOrderDetail, shipOrder, deleteOrder } from '@/api/adminOrder'
import PageHeader from '@/components/admin/PageHeader.vue'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const status = ref()
const orderNo = ref('')
const loading = ref(false)

const detailVisible = ref(false)
const detail = ref(null)
const defaultCover = '/vite.svg'

const statusText = (val) => {
  const map = { 0: '待付款', 1: '已付款', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[val] || '未知'
}

const statusType = (val) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info', 3: 'success', 4: 'danger' }
  return map[val] || 'info'
}

const formatPrice = (price) => Number(price || 0).toFixed(2)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminOrderList({
      page: page.value,
      size: size.value,
      status: status.value,
      orderNo: orderNo.value || undefined
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
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

const openDetail = async (row) => {
  const res = await getAdminOrderDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
}

const ship = async (row) => {
  try {
    await ElMessageBox.confirm('确认对该订单发货？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await shipOrder(row.id)
    ElMessage.success(res.message || '发货成功')
    loadData()
  } catch {
    // ignore cancel
  }
}

const removeOrder = async (row) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteOrder(row.id)
    ElMessage.success(res.message || '删除成功')
    loadData()
  } catch {
    // ignore cancel
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.detail-wrap {
  display: grid;
  gap: 12px;
}

.detail-info {
  display: grid;
  gap: 6px;
  font-size: 14px;
  color: #4b5563;
}

.detail-info span {
  color: var(--admin-text-secondary);
}

.detail-items .item {
  display: grid;
  grid-template-columns: 56px 1fr auto auto;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}

.detail-items img {
  width: 48px;
  height: 68px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #edf2f8;
}

.detail-items .title {
  font-weight: 600;
  color: #111827;
}

.detail-items .meta {
  color: #6b7280;
  font-size: 12px;
}

.detail-items .price {
  color: var(--admin-danger);
  font-weight: 600;
}
</style>
