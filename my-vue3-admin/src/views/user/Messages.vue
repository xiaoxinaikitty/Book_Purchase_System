<template>
  <div class="messages-page">
    <header class="page-header">
      <div>
        <el-button text class="back-btn" @click="goBack">← 返回</el-button>
        <h1>站内消息</h1>
        <p>查看私信、系统通知与互动消息</p>
      </div>
      <div class="header-actions">
        <el-tag type="info" effect="plain">未读 {{ unreadCount }}</el-tag>
        <el-button type="primary" @click="activeTab = 'compose'">写消息</el-button>
      </div>
    </header>

    <el-card class="table-card" shadow="never">
      <el-tabs v-model="activeTab" class="message-tabs">
        <el-tab-pane label="收件箱" name="inbox">
          <div class="tab-toolbar">
            <el-select v-model="status" placeholder="状态" clearable @change="handleSearch">
              <el-option label="未读" :value="0" />
              <el-option label="已读" :value="1" />
            </el-select>
            <el-button @click="handleSearch">刷新</el-button>
          </div>
          <el-table :data="inboxList" v-loading="loadingInbox" stripe>
            <el-table-column prop="senderName" label="发送者" min-width="120" />
            <el-table-column prop="title" label="标题" min-width="200" />
            <el-table-column prop="createTime" label="时间" min-width="160" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 1 ? 'success' : 'warning'">
                  {{ row.status === 1 ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-button size="small" @click="openDetail(row)">查看</el-button>
                <el-button
                  size="small"
                  type="success"
                  v-if="row.status === 0"
                  @click="markRead(row)"
                >
                  标记已读
                </el-button>
                <el-button size="small" type="danger" @click="removeMessage(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :page-sizes="[10, 20, 50]"
              :total="inboxTotal"
              :page-size="inboxSize"
              :current-page="inboxPage"
              @size-change="handleInboxSizeChange"
              @current-change="handleInboxPageChange"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="发件箱" name="sent">
          <el-table :data="sentList" v-loading="loadingSent" stripe>
            <el-table-column prop="receiverName" label="接收者" min-width="120" />
            <el-table-column prop="title" label="标题" min-width="200" />
            <el-table-column prop="createTime" label="时间" min-width="160" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button size="small" @click="openDetail(row)">查看</el-button>
                <el-button size="small" type="danger" @click="removeMessage(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :page-sizes="[10, 20, 50]"
              :total="sentTotal"
              :page-size="sentSize"
              :current-page="sentPage"
              @size-change="handleSentSizeChange"
              @current-change="handleSentPageChange"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="写消息" name="compose">
          <div class="compose-card">
            <el-form :model="composeForm" label-width="90px">
              <el-form-item label="接收者ID">
                <el-input v-model="composeForm.receiverId" placeholder="请输入接收者用户ID" />
              </el-form-item>
              <el-form-item label="标题">
                <el-input v-model="composeForm.title" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="内容">
                <el-input
                  v-model="composeForm.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入消息内容"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitCompose">发送</el-button>
                <el-button @click="resetCompose">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="detailVisible" title="消息详情" width="600px">
      <div v-if="detail" class="detail-content">
        <h2>{{ detail.title }}</h2>
        <p class="detail-meta">
          <span>发送者：{{ detail.senderName || detail.senderId }}</span>
          <span>接收者：{{ detail.receiverName || detail.receiverId }}</span>
          <span>时间：{{ detail.createTime }}</span>
        </p>
        <div class="detail-body">{{ detail.content }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getInbox,
  getSent,
  sendMessage,
  getMessageDetail,
  markMessageRead,
  deleteMessage,
  getMessageUnreadCount
} from '@/api/message'

const router = useRouter()

const activeTab = ref('inbox')
const status = ref()
const unreadCount = ref(0)

const inboxList = ref([])
const inboxTotal = ref(0)
const inboxPage = ref(1)
const inboxSize = ref(10)
const loadingInbox = ref(false)

const sentList = ref([])
const sentTotal = ref(0)
const sentPage = ref(1)
const sentSize = ref(10)
const loadingSent = ref(false)

const composeForm = ref({
  receiverId: '',
  title: '',
  content: ''
})

const detailVisible = ref(false)
const detail = ref(null)

const loadUnread = async () => {
  const res = await getMessageUnreadCount()
  unreadCount.value = res.data || 0
}

const loadInbox = async () => {
  loadingInbox.value = true
  try {
    const res = await getInbox({
      page: inboxPage.value,
      size: inboxSize.value,
      status: status.value
    })
    inboxList.value = res.data.records || []
    inboxTotal.value = res.data.total || 0
  } finally {
    loadingInbox.value = false
  }
}

const loadSent = async () => {
  loadingSent.value = true
  try {
    const res = await getSent({
      page: sentPage.value,
      size: sentSize.value
    })
    sentList.value = res.data.records || []
    sentTotal.value = res.data.total || 0
  } finally {
    loadingSent.value = false
  }
}

const handleSearch = () => {
  inboxPage.value = 1
  loadInbox()
}

const handleInboxPageChange = (val) => {
  inboxPage.value = val
  loadInbox()
}

const handleInboxSizeChange = (val) => {
  inboxSize.value = val
  inboxPage.value = 1
  loadInbox()
}

const handleSentPageChange = (val) => {
  sentPage.value = val
  loadSent()
}

const handleSentSizeChange = (val) => {
  sentSize.value = val
  sentPage.value = 1
  loadSent()
}

const openDetail = async (row) => {
  const res = await getMessageDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
  await loadInbox()
  await loadUnread()
}

const markRead = async (row) => {
  await markMessageRead(row.id)
  ElMessage.success('已标记为已读')
  await loadInbox()
  await loadUnread()
}

const removeMessage = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMessage(row.id)
    ElMessage.success('删除成功')
    await loadInbox()
    await loadSent()
    await loadUnread()
  } catch {
    // ignore
  }
}

const submitCompose = async () => {
  if (!composeForm.value.receiverId || !composeForm.value.title || !composeForm.value.content) {
    ElMessage.warning('请完整填写信息')
    return
  }
  await sendMessage({
    receiverId: Number(composeForm.value.receiverId),
    title: composeForm.value.title,
    content: composeForm.value.content
  })
  ElMessage.success('发送成功')
  resetCompose()
  activeTab.value = 'sent'
  await loadSent()
}

const resetCompose = () => {
  composeForm.value = {
    receiverId: '',
    title: '',
    content: ''
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
  await loadInbox()
  await loadSent()
  await loadUnread()
})
</script>

<style scoped>
.messages-page {
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

.tab-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.compose-card {
  padding: 8px 12px;
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
