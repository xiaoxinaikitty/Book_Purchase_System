<template>
  <div class="recommend-config">
    <header class="page-header">
      <div>
        <h1>推荐配置</h1>
        <p>调整 KNN 参数与行为权重</p>
      </div>
      <div class="actions">
        <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
      </div>
    </header>

    <el-card class="config-card" shadow="never" v-loading="loading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="K 值" prop="kValue">
          <el-input-number v-model="form.kValue" :min="1" :max="50" />
        </el-form-item>

        <el-form-item label="相似度算法" prop="similarityType">
          <el-select v-model="form.similarityType" placeholder="请选择算法">
            <el-option label="余弦相似度 (cosine)" value="cosine" />
            <el-option label="欧氏距离 (euclidean)" value="euclidean" />
          </el-select>
        </el-form-item>

        <el-form-item label="最小相似度" prop="minSimilarity">
          <el-input-number v-model="form.minSimilarity" :min="0" :max="1" :step="0.05" />
          <span class="tip">过滤相似度过低的用户</span>
        </el-form-item>

        <el-divider>行为权重（0-5）</el-divider>

        <el-form-item label="评分权重" prop="weightReview">
          <el-slider v-model="form.weightReview" :min="0" :max="5" :step="0.1" show-input />
        </el-form-item>

        <el-form-item label="收藏权重" prop="weightFavorite">
          <el-slider v-model="form.weightFavorite" :min="0" :max="5" :step="0.1" show-input />
        </el-form-item>

        <el-form-item label="浏览权重" prop="weightBrowse">
          <el-slider v-model="form.weightBrowse" :min="0" :max="5" :step="0.1" show-input />
        </el-form-item>

        <el-form-item label="购买权重" prop="weightPurchase">
          <el-slider v-model="form.weightPurchase" :min="0" :max="5" :step="0.1" show-input />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRecommendConfig, updateRecommendConfig } from '@/api/adminRecommendConfig'

const loading = ref(false)
const saving = ref(false)
const formRef = ref(null)

const form = reactive({
  kValue: 5,
  similarityType: 'cosine',
  minSimilarity: 0.1,
  weightReview: 1.0,
  weightFavorite: 0.7,
  weightBrowse: 0.3,
  weightPurchase: 1.2
})

const rules = {
  kValue: [{ required: true, message: '请输入 K 值', trigger: 'blur' }],
  similarityType: [{ required: true, message: '请选择相似度算法', trigger: 'change' }],
  minSimilarity: [{ required: true, message: '请输入最小相似度', trigger: 'blur' }]
}

const loadConfig = async () => {
  loading.value = true
  try {
    const res = await getRecommendConfig()
    Object.assign(form, res.data || {})
  } catch (error) {
    console.error('获取推荐配置失败:', error)
  } finally {
    loading.value = false
  }
}

const saveConfig = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const res = await updateRecommendConfig(form)
      ElMessage.success(res.message || '保存成功')
      Object.assign(form, res.data || {})
    } catch (error) {
      console.error('更新推荐配置失败:', error)
    } finally {
      saving.value = false
    }
  })
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.recommend-config {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 12px;
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

.config-card {
  border-radius: 16px;
  border: none;
  padding: 8px 4px;
}

.tip {
  margin-left: 10px;
  color: #9ca3af;
  font-size: 12px;
}
</style>
