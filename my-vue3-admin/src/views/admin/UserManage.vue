<template>
  <section class="admin-page">
    <PageHeader title="用户管理" description="管理用户账户状态、资料与安全操作">
      <template #actions>
        <div class="admin-filter-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索用户名/昵称/邮箱"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-grid-3">
      <div class="admin-card admin-card-inner summary-card">
        <span class="label">当前页用户</span>
        <strong>{{ list.length }}</strong>
      </div>
      <div class="admin-card admin-card-inner summary-card">
        <span class="label">管理员数量</span>
        <strong>{{ adminCount }}</strong>
      </div>
      <div class="admin-card admin-card-inner summary-card">
        <span class="label">禁用用户</span>
        <strong>{{ disabledCount }}</strong>
      </div>
    </div>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="130" />
        <el-table-column prop="nickname" label="昵称" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.role === 1 ? 'warning' : 'success'">
              {{ row.role === 1 ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="260">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              :disabled="row.id === userStore.userInfo.userId"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="info" @click="resetPassword(row)">重置密码</el-button>
            <el-button
              size="small"
              type="danger"
              :disabled="row.id === userStore.userInfo.userId"
              @click="removeUser(row)"
            >
              删除
            </el-button>
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

    <el-dialog v-model="detailVisible" title="用户详情" width="420px">
      <div class="detail-grid" v-if="detail">
        <div><span>用户ID：</span>{{ detail.id }}</div>
        <div><span>用户名：</span>{{ detail.username }}</div>
        <div><span>昵称：</span>{{ detail.nickname }}</div>
        <div><span>邮箱：</span>{{ detail.email }}</div>
        <div><span>手机号：</span>{{ detail.phone }}</div>
        <div><span>角色：</span>{{ detail.role === 1 ? '管理员' : '普通用户' }}</div>
        <div><span>状态：</span>{{ detail.status === 1 ? '正常' : '禁用' }}</div>
        <div><span>创建时间：</span>{{ detail.createTime }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  getUserList,
  getUserDetail,
  updateUserStatus,
  resetUserPassword,
  deleteUser
} from '@/api/adminUser'
import PageHeader from '@/components/admin/PageHeader.vue'

const userStore = useUserStore()
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)

const detailVisible = ref(false)
const detail = ref(null)

const adminCount = computed(() => list.value.filter((item) => item.role === 1).length)
const disabledCount = computed(() => list.value.filter((item) => item.status !== 1).length)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
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

const openDetail = async (row) => {
  try {
    const res = await getUserDetail(row.id)
    detail.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
  }
}

const toggleStatus = async (row) => {
  const targetStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(`确定要${targetStatus === 1 ? '启用' : '禁用'}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await updateUserStatus(row.id, targetStatus)
    ElMessage.success(res.message || '操作成功')
    loadData()
  } catch {
    // ignore cancel
  }
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户密码吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await resetUserPassword(row.id)
    ElMessageBox.alert(`新密码：${res.data}`, '重置成功', {
      confirmButtonText: '知道了',
      type: 'success'
    })
  } catch {
    // ignore cancel
  }
}

const removeUser = async (row) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteUser(row.id)
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
.summary-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-card .label {
  color: var(--admin-text-secondary);
  font-size: 13px;
}

.summary-card strong {
  font-size: 30px;
  color: var(--admin-text-main);
}

.detail-grid {
  display: grid;
  gap: 10px;
  color: #374151;
  font-size: 14px;
}

.detail-grid span {
  color: #6b7280;
}
</style>
