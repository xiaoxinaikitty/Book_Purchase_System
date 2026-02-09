<template>
  <div class="admin-books">
    <header class="page-header">
      <div>
        <h1>图书管理</h1>
        <p>维护图书信息与上下架状态</p>
      </div>
      <div class="actions">
        <el-input
          v-model="keyword"
          placeholder="搜索书名/作者/出版社"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-select v-model="categoryId" placeholder="分类" clearable>
          <el-option label="全部分类" :value="undefined" />
          <el-option
            v-for="item in categories"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select v-model="status" placeholder="状态" clearable>
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">筛选</el-button>
        <el-button type="success" @click="openAdd">新增图书</el-button>
      </div>
    </header>

    <el-card class="table-card" shadow="never">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <img class="cover" :src="row.coverImage || defaultCover" alt="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" min-width="120" />
        <el-table-column prop="categoryName" label="分类" min-width="120" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" />
        <el-table-column prop="sales" label="销量" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="removeBook(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="form.publishDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
          />
        </el-form-item>
        <el-form-item label="封面URL" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="https://example.com/cover.jpg" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminBookList,
  addBook,
  updateBook,
  deleteBook,
  updateBookStatus
} from '@/api/adminBook'
import { getAdminCategoryList } from '@/api/adminCategory'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const categoryId = ref()
const status = ref()
const loading = ref(false)

const categories = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增图书')
const saving = ref(false)
const formRef = ref(null)

const defaultCover = '/vite.svg'

const form = reactive({
  id: null,
  title: '',
  author: '',
  publisher: '',
  isbn: '',
  categoryId: null,
  price: 0,
  stock: 0,
  coverImage: '',
  description: '',
  publishDate: '',
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadCategories = async () => {
  try {
    const res = await getAdminCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminBookList({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined,
      categoryId: categoryId.value,
      status: status.value
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取图书列表失败:', error)
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
  form.id = null
  form.title = ''
  form.author = ''
  form.publisher = ''
  form.isbn = ''
  form.categoryId = null
  form.price = 0
  form.stock = 0
  form.coverImage = ''
  form.description = ''
  form.publishDate = ''
  form.status = 1
}

const openAdd = () => {
  dialogTitle.value = '新增图书'
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  dialogTitle.value = '编辑图书'
  form.id = row.id
  form.title = row.title
  form.author = row.author
  form.publisher = row.publisher
  form.isbn = row.isbn
  form.categoryId = row.categoryId
  form.price = row.price
  form.stock = row.stock
  form.coverImage = row.coverImage
  form.description = row.description
  form.publishDate = row.publishDate
  form.status = row.status
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (form.id) {
        const res = await updateBook(form)
        ElMessage.success(res.message || '更新成功')
      } else {
        const res = await addBook(form)
        ElMessage.success(res.message || '添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('保存图书失败:', error)
    } finally {
      saving.value = false
    }
  })
}

const toggleStatus = async (row) => {
  const targetStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(
      `确定要${targetStatus === 1 ? '上架' : '下架'}该图书吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    const res = await updateBookStatus(row.id, targetStatus)
    ElMessage.success(res.message || '操作成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

const removeBook = async (row) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteBook(row.id)
    ElMessage.success(res.message || '删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除图书失败:', error)
    }
  }
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.admin-books {
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

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.table-card {
  border-radius: 14px;
}

.cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 900px) {
  .actions {
    width: 100%;
  }
}
</style>
