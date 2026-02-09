<template>
  <div class="address-page">
    <header class="page-header">
      <div>
        <h1>收货地址</h1>
        <p>管理收货信息</p>
      </div>
      <el-button type="primary" @click="openAdd">新增地址</el-button>
    </header>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="receiver" label="收货人" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column label="地址" min-width="260">
          <template #default="{ row }">
            {{ row.province }}{{ row.city }}{{ row.district }}{{ row.detail }}
          </template>
        </el-table-column>
        <el-table-column label="默认" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.isDefault === 1 ? 'success' : 'info'">
              {{ row.isDefault === 1 ? '默认' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              type="warning"
              v-if="row.isDefault !== 1"
              @click="setDefault(row)"
            >
              设为默认
            </el-button>
            <el-button size="small" type="danger" @click="removeAddress(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="form.receiver" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" />
        </el-form-item>
        <el-form-item label="设为默认" prop="isDefault">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/address'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')
const saving = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

const rules = {
  receiver: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    list.value = res.data || []
  } catch (error) {
    console.error('获取地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.receiver = ''
  form.phone = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.detail = ''
  form.isDefault = 0
}

const openAdd = () => {
  dialogTitle.value = '新增地址'
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  dialogTitle.value = '编辑地址'
  form.id = row.id
  form.receiver = row.receiver
  form.phone = row.phone
  form.province = row.province
  form.city = row.city
  form.district = row.district
  form.detail = row.detail
  form.isDefault = row.isDefault || 0
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (form.id) {
        const res = await updateAddress(form)
        ElMessage.success(res.message || '更新成功')
      } else {
        const res = await addAddress(form)
        ElMessage.success(res.message || '添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('保存地址失败:', error)
    } finally {
      saving.value = false
    }
  })
}

const setDefault = async (row) => {
  try {
    const res = await setDefaultAddress(row.id)
    ElMessage.success(res.message || '设置成功')
    loadData()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

const removeAddress = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteAddress(row.id)
    ElMessage.success(res.message || '删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
    }
  }
}

onMounted(loadData)
</script>

<style scoped>
.address-page {
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
</style>
