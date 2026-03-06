<template>
  <section class="admin-page">
    <PageHeader title="评价管理" description="查看并处理用户评价">
      <template #actions>
        <div class="admin-filter-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索图书/用户/内容"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="rating" placeholder="评分" clearable @change="handleSearch">
            <el-option label="5 分" :value="5" />
            <el-option label="4 分" :value="4" />
            <el-option label="3 分" :value="3" />
            <el-option label="2 分" :value="2" />
            <el-option label="1 分" :value="1" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" />
        <el-table-column prop="nickname" label="用户" min-width="120" />
        <el-table-column label="评分" width="100">
          <template #default="{ row }">
            <el-rate :model-value="row.rating" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" min-width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="removeReview(row)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/admin/PageHeader.vue'
import { getAdminReviewList, deleteAdminReview } from '@/api/adminReview'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const rating = ref()
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminReviewList({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined,
      rating: rating.value
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

const removeReview = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAdminReview(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ignore
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-filter-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
