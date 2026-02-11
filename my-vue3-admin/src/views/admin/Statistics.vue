<template>
  <div class="admin-statistics">
    <header class="page-header">
      <div>
        <h1>数据统计</h1>
        <p>系统关键指标与趋势概览</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="loadAll">刷新数据</el-button>
      </div>
    </header>

    <section class="overview" v-loading="loading.overview">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #6366f1, #8b5cf6);">
          <el-icon :size="26"><UserFilled /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.userCount }}</span>
          <span class="stat-label">用户总数</span>
          <span class="stat-sub">今日新增 {{ overview.todayUserCount }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f97316, #fb7185);">
          <el-icon :size="26"><Reading /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.bookCount }}</span>
          <span class="stat-label">图书总数</span>
          <span class="stat-sub">热销持续增长</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #22c55e, #14b8a6);">
          <el-icon :size="26"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.orderCount }}</span>
          <span class="stat-label">订单总数</span>
          <span class="stat-sub">今日新增 {{ overview.todayOrderCount }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #3b82f6, #06b6d4);">
          <el-icon :size="26"><Coin /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ formatPrice(overview.totalSales) }}</span>
          <span class="stat-label">销售总额</span>
          <span class="stat-sub">今日 ¥{{ formatPrice(overview.todaySales) }}</span>
        </div>
      </div>
    </section>

    <section class="section sales">
      <div class="section-header">
        <div>
          <h2>销售趋势</h2>
          <p>按日期统计的销售额</p>
        </div>
        <el-date-picker
          v-model="salesRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadSales"
        />
      </div>
      <el-card class="section-card" shadow="never" v-loading="loading.sales">
        <div class="sales-chart">
          <div v-for="item in salesList" :key="item.date" class="sales-row">
            <div class="sales-label">{{ item.date }}</div>
            <div class="sales-bar">
              <div
                class="bar-fill"
                :style="{ width: barWidth(item.totalSales) + '%' }"
              ></div>
            </div>
            <div class="sales-value">¥{{ formatPrice(item.totalSales) }}</div>
          </div>
        </div>
      </el-card>
    </section>

    <section class="section grid-2">
      <el-card class="section-card" shadow="never" v-loading="loading.rank">
        <div class="section-title">
          <h3>销量排行</h3>
          <span>Top {{ salesRank.length }}</span>
        </div>
        <el-table :data="salesRank" size="small" stripe>
          <el-table-column type="index" width="50" label="#" />
          <el-table-column prop="title" label="图书" />
          <el-table-column prop="sales" label="销量" width="80" />
          <el-table-column label="价格" width="90">
            <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="section-card" shadow="never" v-loading="loading.category">
        <div class="section-title">
          <h3>分类销量</h3>
          <span>按分类聚合</span>
        </div>
        <el-table :data="categorySales" size="small" stripe>
          <el-table-column prop="categoryName" label="分类" />
          <el-table-column prop="totalQuantity" label="销量" width="90" />
          <el-table-column label="销售额" width="120">
            <template #default="{ row }">¥{{ formatPrice(row.totalSales) }}</template>
          </el-table-column>
        </el-table>
      </el-card>
    </section>

    <section class="section">
      <div class="section-header">
        <div>
          <h2>用户增长</h2>
          <p>最近 {{ growthDays }} 天新增用户</p>
        </div>
        <el-select v-model="growthDays" @change="loadUserGrowth" style="width: 140px">
          <el-option :value="7" label="近 7 天" />
          <el-option :value="14" label="近 14 天" />
          <el-option :value="30" label="近 30 天" />
        </el-select>
      </div>
      <el-card class="section-card" shadow="never" v-loading="loading.growth">
        <el-table :data="userGrowth" size="small" stripe>
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="userCount" label="新增用户" width="100" />
        </el-table>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { UserFilled, Reading, Document, Coin } from '@element-plus/icons-vue'
import {
  getOverview,
  getSalesStatistics,
  getSalesRank,
  getCategorySales,
  getUserGrowth
} from '@/api/adminStatistics'

const overview = reactive({
  userCount: 0,
  bookCount: 0,
  orderCount: 0,
  totalSales: 0,
  todayUserCount: 0,
  todayOrderCount: 0,
  todaySales: 0
})

const salesList = ref([])
const salesRank = ref([])
const categorySales = ref([])
const userGrowth = ref([])

const loading = reactive({
  overview: false,
  sales: false,
  rank: false,
  category: false,
  growth: false
})

const growthDays = ref(7)

const buildDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const defaultRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(end.getDate() - 6)
  return [buildDate(start), buildDate(end)]
}

const salesRange = ref(defaultRange())

const maxSales = computed(() => {
  const values = salesList.value.map(item => Number(item.totalSales || 0))
  return Math.max(...values, 1)
})

const barWidth = (value) => {
  const num = Number(value || 0)
  return Math.min((num / maxSales.value) * 100, 100)
}

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const loadOverview = async () => {
  loading.overview = true
  try {
    const res = await getOverview()
    Object.assign(overview, res.data || {})
  } catch (error) {
    console.error('获取概览失败:', error)
  } finally {
    loading.overview = false
  }
}

const loadSales = async () => {
  loading.sales = true
  try {
    const [startDate, endDate] = salesRange.value || []
    const res = await getSalesStatistics({ startDate, endDate })
    salesList.value = res.data || []
  } catch (error) {
    console.error('获取销售统计失败:', error)
  } finally {
    loading.sales = false
  }
}

const loadSalesRank = async () => {
  loading.rank = true
  try {
    const res = await getSalesRank({ limit: 10 })
    salesRank.value = res.data || []
  } catch (error) {
    console.error('获取销量排行失败:', error)
  } finally {
    loading.rank = false
  }
}

const loadCategorySales = async () => {
  loading.category = true
  try {
    const res = await getCategorySales()
    categorySales.value = res.data || []
  } catch (error) {
    console.error('获取分类销量失败:', error)
  } finally {
    loading.category = false
  }
}

const loadUserGrowth = async () => {
  loading.growth = true
  try {
    const res = await getUserGrowth({ days: growthDays.value })
    userGrowth.value = res.data || []
  } catch (error) {
    console.error('获取用户增长失败:', error)
  } finally {
    loading.growth = false
  }
}

const loadAll = () => {
  loadOverview()
  loadSales()
  loadSalesRank()
  loadCategorySales()
  loadUserGrowth()
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped>
.admin-statistics {
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

.overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  display: flex;
  gap: 14px;
  align-items: center;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.06);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.stat-sub {
  font-size: 12px;
  color: #9ca3af;
}

.section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}

.section-header h2 {
  font-size: 18px;
  color: #111827;
  margin-bottom: 4px;
}

.section-header p {
  font-size: 12px;
  color: #6b7280;
}

.section-card {
  border-radius: 14px;
  border: none;
}

.sales-chart {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sales-row {
  display: grid;
  grid-template-columns: 100px 1fr 120px;
  gap: 12px;
  align-items: center;
}

.sales-label {
  font-size: 12px;
  color: #6b7280;
}

.sales-bar {
  background: #eef2ff;
  border-radius: 999px;
  height: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #22c55e);
  border-radius: 999px;
}

.sales-value {
  text-align: right;
  font-size: 12px;
  color: #111827;
}

.grid-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 12px;
}

.section-title h3 {
  font-size: 16px;
  color: #111827;
}

.section-title span {
  font-size: 12px;
  color: #9ca3af;
}

@media (max-width: 1200px) {
  .overview {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 960px) {
  .grid-2 {
    grid-template-columns: 1fr;
  }

  .sales-row {
    grid-template-columns: 90px 1fr 90px;
  }
}

@media (max-width: 640px) {
  .overview {
    grid-template-columns: 1fr;
  }
}
</style>
