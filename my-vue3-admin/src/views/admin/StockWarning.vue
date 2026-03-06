<template>
  <section class="admin-page">
    <PageHeader title="库存预警" description="关注低库存图书，避免断货">
      <template #actions>
        <div class="admin-filter-bar">
          <span class="threshold-label">库存阈值</span>
          <el-input-number v-model="threshold" :min="0" :max="9999" />
          <el-button type="primary" @click="handleSearch">刷新</el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="title" label="图书名称" min-width="200" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
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
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageHeader from '@/components/admin/PageHeader.vue'
import { getLowStockBooks } from '@/api/adminBook'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const threshold = ref(20)
const loading = ref(false)

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLowStockBooks({
      page: page.value,
      size: size.value,
      threshold: threshold.value
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.threshold-label {
  color: var(--admin-text-secondary);
  font-size: 13px;
}
</style>
