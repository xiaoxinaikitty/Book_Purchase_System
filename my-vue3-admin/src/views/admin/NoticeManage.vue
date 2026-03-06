<template>
  <section class="admin-page">
    <PageHeader title="公告管理" description="发布与维护平台公告、活动信息">
      <template #actions>
        <div class="admin-filter-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索标题/内容"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="status" placeholder="状态" clearable @change="handleSearch">
            <el-option label="已发布" :value="1" />
            <el-option label="已下线" :value="0" />
          </el-select>
          <el-button type="primary" @click="openCreate">新增公告</el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '已下线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="110" />
        <el-table-column prop="publishTime" label="发布时间" min-width="160" />
        <el-table-column prop="updateTime" label="更新时间" min-width="160" />
        <el-table-column label="操作" min-width="260">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下线' : '发布' }}
            </el-button>
            <el-button size="small" type="danger" @click="removeNotice(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态">
            <el-option label="已发布" :value="1" />
            <el-option label="已下线" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="form.priority" :min="0" :max="99" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/admin/PageHeader.vue'
import {
  getAdminNoticeList,
  addNotice,
  updateNotice,
  updateNoticeStatus,
  deleteNotice
} from '@/api/adminNotice'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const status = ref()
const loading = ref(false)

const dialogVisible = ref(false)
const form = ref({
  id: null,
  title: '',
  content: '',
  status: 1,
  priority: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminNoticeList({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined,
      status: status.value
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

const resetForm = () => {
  form.value = {
    id: null,
    title: '',
    content: '',
    status: 1,
    priority: 0
  }
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = {
    id: row.id,
    title: row.title,
    content: row.content,
    status: row.status ?? 1,
    priority: row.priority ?? 0
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  if (form.value.id) {
    await updateNotice(form.value)
    ElMessage.success('更新成功')
  } else {
    await addNotice(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const toggleStatus = async (row) => {
  const target = row.status === 1 ? 0 : 1
  const text = target === 1 ? '发布' : '下线'
  try {
    await ElMessageBox.confirm(`确定要${text}该公告吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateNoticeStatus(row.id, target)
    ElMessage.success('状态更新成功')
    loadData()
  } catch {
    // ignore
  }
}

const removeNotice = async (row) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteNotice(row.id)
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
