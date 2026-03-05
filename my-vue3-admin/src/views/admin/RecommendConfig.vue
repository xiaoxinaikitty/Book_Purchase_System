<template>
  <section class="admin-page">
    <PageHeader title="推荐配置" description="调整推荐算法参数与行为权重，影响个性化推荐结果">
      <template #actions>
        <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
      </template>
    </PageHeader>

    <div class="admin-grid-2">
      <div class="admin-card admin-card-inner">
        <div class="panel-title">算法参数</div>
        <div class="panel-sub">配置 KNN 核心规则与相似度筛选条件</div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" v-loading="loading">
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
          </el-form-item>
        </el-form>
      </div>

      <div class="admin-card admin-card-inner">
        <div class="panel-title">行为权重</div>
        <div class="panel-sub">范围 0-5，数值越高影响越强</div>
        <el-form :model="form" label-width="120px" v-loading="loading">
          <el-form-item label="评分权重">
            <el-slider v-model="form.weightReview" :min="0" :max="5" :step="0.1" show-input />
          </el-form-item>
          <el-form-item label="收藏权重">
            <el-slider v-model="form.weightFavorite" :min="0" :max="5" :step="0.1" show-input />
          </el-form-item>
          <el-form-item label="浏览权重">
            <el-slider v-model="form.weightBrowse" :min="0" :max="5" :step="0.1" show-input />
          </el-form-item>
          <el-form-item label="购买权重">
            <el-slider v-model="form.weightPurchase" :min="0" :max="5" :step="0.1" show-input />
          </el-form-item>
        </el-form>

        <div class="weight-preview">
          <div class="row"><span>评分</span><strong>{{ form.weightReview.toFixed(1) }}</strong></div>
          <div class="row"><span>收藏</span><strong>{{ form.weightFavorite.toFixed(1) }}</strong></div>
          <div class="row"><span>浏览</span><strong>{{ form.weightBrowse.toFixed(1) }}</strong></div>
          <div class="row"><span>购买</span><strong>{{ form.weightPurchase.toFixed(1) }}</strong></div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRecommendConfig, updateRecommendConfig } from '@/api/adminRecommendConfig'
import PageHeader from '@/components/admin/PageHeader.vue'

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
  } finally {
    loading.value = false
  }
}

const saveConfig = async () => {
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
    const res = await updateRecommendConfig(form)
    ElMessage.success(res.message || '保存成功')
    Object.assign(form, res.data || {})
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.panel-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--admin-text-main);
}

.panel-sub {
  margin-top: 4px;
  margin-bottom: 14px;
  color: var(--admin-text-light);
  font-size: 12px;
}

.weight-preview {
  margin-top: 8px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}

.weight-preview .row {
  border: 1px solid #e6edf7;
  border-radius: 10px;
  background: #f8fbff;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}

.weight-preview span {
  color: var(--admin-text-secondary);
}

.weight-preview strong {
  color: var(--admin-text-main);
}

@media (max-width: 840px) {
  .weight-preview {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
