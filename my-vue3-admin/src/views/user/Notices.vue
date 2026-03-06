<template>
  <div class="notices-page">
    <header class="page-header">
      <div>
        <el-button text class="back-btn" @click="goBack">← 返回</el-button>
        <h1>公告中心</h1>
        <p>查看平台最新通知与活动信息</p>
      </div>
      <div class="header-actions">
        <el-tag type="info" effect="plain">未读 {{ unreadCount }}</el-tag>
        <el-input
          v-model="keyword"
          placeholder="搜索公告"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-switch v-model="onlyUnread" active-text="只看未读" @change="handleFilter" />
      </div>
    </header>

    <el-card class="table-card" shadow="never">
      <el-table :data="displayList" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="publishTime" label="发布时间" min-width="160" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="row.readFlag === 1 ? 'success' : 'warning'">
              {{ row.readFlag === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">查看</el-button>
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

    <el-dialog v-model="detailVisible" title="公告详情" width="600px">
      <div v-if="detail" class="detail-content">
        <h2>{{ detail.title }}</h2>
        <p class="detail-time">{{ detail.publishTime || detail.createTime }}</p>
        <div class="detail-body">{{ detail.content }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticeList, getNoticeDetail, markNoticeRead, getUnreadNoticeCount } from '@/api/notice'

const router = useRouter()

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const onlyUnread = ref(false)
const unreadCount = ref(0)
const loading = ref(false)

const detailVisible = ref(false)
const detail = ref(null)

const displayList = computed(() => {
  if (!onlyUnread.value) return list.value
  return list.value.filter((item) => item.readFlag !== 1)
})

const loadUnreadCount = async () => {
  const res = await getUnreadNoticeCount()
  unreadCount.value = res.data || 0
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined
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

const openDetail = async (row) => {
  const res = await getNoticeDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
  if (row.readFlag !== 1) {
    await markNoticeRead(row.id)
    await loadData()
    await loadUnreadCount()
  }
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}

onMounted(async () => {
  await loadData()
  await loadUnreadCount()
})
</script>

<style scoped>
.notices-page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
  flex-wrap: wrap;
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
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.back-btn {
  padding-left: 0;
  margin-bottom: 6px;
}

.table-card {
  border-radius: 14px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.detail-content h2 {
  margin-bottom: 8px;
  color: #111827;
}

.detail-time {
  color: #9ca3af;
  font-size: 12px;
  margin-bottom: 12px;
}

.detail-body {
  color: #374151;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
