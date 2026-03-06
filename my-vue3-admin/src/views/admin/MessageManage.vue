<template>
  <section class="admin-page">
    <PageHeader title="站内消息" description="向用户发送私信或群发通知">
      <template #actions>
        <div class="admin-filter-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索标题/内容/接收者"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="openSend">发送消息</el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="receiverName" label="接收者" min-width="140" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="createTime" label="时间" min-width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">查看</el-button>
            <el-button size="small" type="danger" @click="removeMessage(row)">删除</el-button>
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

    <el-dialog v-model="sendVisible" title="发送消息" width="520px">
      <el-form :model="sendForm" label-width="100px">
        <el-form-item label="群发">
          <el-switch v-model="sendForm.broadcast" active-text="发送给全部用户" />
        </el-form-item>
        <el-form-item label="接收者ID" v-if="!sendForm.broadcast">
          <el-input v-model="sendForm.receiverId" placeholder="请输入接收者用户ID" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="sendForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="sendForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入消息内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSend">发送</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="消息详情" width="600px">
      <div v-if="detail" class="detail-content">
        <h2>{{ detail.title }}</h2>
        <p class="detail-meta">
          <span>接收者：{{ detail.receiverName || detail.receiverId }}</span>
          <span>时间：{{ detail.createTime }}</span>
          <span>状态：{{ detail.status === 1 ? '已读' : '未读' }}</span>
        </p>
        <div class="detail-body">{{ detail.content }}</div>
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
import PageHeader from '@/components/admin/PageHeader.vue'
import { getAdminMessageList, sendAdminMessage, deleteAdminMessage } from '@/api/adminMessage'
import { getMessageDetail } from '@/api/message'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)

const sendVisible = ref(false)
const detailVisible = ref(false)
const detail = ref(null)

const sendForm = ref({
  broadcast: false,
  receiverId: '',
  title: '',
  content: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminMessageList({
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

const handlePageChange = (val) => {
  page.value = val
  loadData()
}

const handleSizeChange = (val) => {
  size.value = val
  page.value = 1
  loadData()
}

const openSend = () => {
  sendForm.value = {
    broadcast: false,
    receiverId: '',
    title: '',
    content: ''
  }
  sendVisible.value = true
}

const submitSend = async () => {
  if (!sendForm.value.title || !sendForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  if (!sendForm.value.broadcast && !sendForm.value.receiverId) {
    ElMessage.warning('请填写接收者ID')
    return
  }
  await sendAdminMessage({
    receiverId: sendForm.value.broadcast ? null : Number(sendForm.value.receiverId),
    title: sendForm.value.title,
    content: sendForm.value.content
  })
  ElMessage.success('发送成功')
  sendVisible.value = false
  loadData()
}

const openDetail = async (row) => {
  const res = await getMessageDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
}

const removeMessage = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAdminMessage(row.id)
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

.detail-content h2 {
  margin-bottom: 8px;
  color: #111827;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
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
