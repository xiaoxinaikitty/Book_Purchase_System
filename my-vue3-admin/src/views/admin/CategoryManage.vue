<template>
  <section class="admin-page">
    <PageHeader title="分类管理" description="维护分类层级与展示顺序">
      <template #actions>
        <el-button type="primary" @click="openAdd">新增分类</el-button>
      </template>
    </PageHeader>

    <div class="admin-card admin-card-inner">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column label="父级分类" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">
              {{ parentName(row.parentId) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="removeCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父级分类">
            <el-option label="一级分类" :value="0" />
            <el-option v-for="item in list" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/adminCategory'
import PageHeader from '@/components/admin/PageHeader.vue'

const list = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const saving = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  parentId: 0,
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const parentName = (parentId) => {
  if (!parentId || parentId === 0) return '一级分类'
  const parent = list.value.find((item) => item.id === parentId)
  return parent ? parent.name : '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminCategoryList()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.parentId = 0
  form.sort = 0
}

const openAdd = () => {
  dialogTitle.value = '新增分类'
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  dialogTitle.value = '编辑分类'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    parentId: row.parentId || 0,
    sort: row.sort || 0
  })
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  let valid = true
  try {
    await formRef.value.validate()
  } catch {
    valid = false
  }
  if (!valid) return

  saving.value = true
  try {
    if (form.id) {
      const res = await updateCategory(form)
      ElMessage.success(res.message || '更新成功')
    } else {
      const res = await addCategory(form)
      ElMessage.success(res.message || '添加成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const removeCategory = async (row) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteCategory(row.id)
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
