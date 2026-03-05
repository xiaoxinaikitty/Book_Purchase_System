<template>
  <section class="admin-page">
    <PageHeader title="运营驾驶舱" description="实时查看核心经营指标、销售走势与用户增长">
      <template #actions>
        <el-date-picker
          v-model="salesRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadSales"
        />
        <el-button type="primary" :icon="Refresh" :loading="isRefreshing" @click="loadAll">
          刷新数据
        </el-button>
      </template>
    </PageHeader>

    <div class="admin-grid-4">
      <KpiCard
        :icon="UserFilled"
        icon-bg="linear-gradient(135deg, #1677ff, #14b8a6)"
        label="用户总数"
        :value="formatNumber(overview.userCount)"
        :footnote="`今日新增 ${formatNumber(overview.todayUserCount)}`"
      />
      <KpiCard
        :icon="Reading"
        icon-bg="linear-gradient(135deg, #6366f1, #a855f7)"
        label="图书总数"
        :value="formatNumber(overview.bookCount)"
        footnote="库存结构保持稳定"
      />
      <KpiCard
        :icon="Document"
        icon-bg="linear-gradient(135deg, #f97316, #ef4444)"
        label="订单总数"
        :value="formatNumber(overview.orderCount)"
        :footnote="`今日订单 ${formatNumber(overview.todayOrderCount)}`"
      />
      <KpiCard
        :icon="Coin"
        icon-bg="linear-gradient(135deg, #00b578, #16a34a)"
        label="累计销售额"
        :value="`¥${formatPrice(overview.totalSales)}`"
        :footnote="`今日销售 ¥${formatPrice(overview.todaySales)}`"
      />
    </div>

    <div class="admin-grid-2">
      <div class="admin-card admin-card-inner">
        <div class="panel-title">销售趋势</div>
        <div class="panel-sub">按天统计区间内销售额变化</div>
        <TrendLineChart :labels="salesLabels" :values="salesValues" />
        <div class="mini-stats">
          <div>
            <span class="mini-label">区间销售额</span>
            <strong>¥{{ formatPrice(totalSalesInRange) }}</strong>
          </div>
          <div>
            <span class="mini-label">峰值日期</span>
            <strong>{{ maxSalesDay }}</strong>
          </div>
        </div>
      </div>

      <div class="admin-card admin-card-inner">
        <div class="panel-title">分类销售占比</div>
        <div class="panel-sub">展示 Top 分类销量分布（环形图）</div>
        <DonutChart :items="categoryDonutItems" center-label="分类销量" />
      </div>
    </div>

    <div class="admin-grid-2">
      <div class="admin-card admin-card-inner">
        <div class="panel-head">
          <div>
            <div class="panel-title">图书销量排行</div>
            <div class="panel-sub">热门图书 Top 8</div>
          </div>
          <el-tag effect="plain" type="info">Top {{ salesRank.length }}</el-tag>
        </div>
        <el-table :data="salesRank" stripe size="small">
          <el-table-column type="index" width="54" label="#" />
          <el-table-column prop="title" label="图书名称" min-width="160" />
          <el-table-column prop="sales" label="销量" width="90" />
          <el-table-column label="金额" width="120">
            <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <div class="admin-card admin-card-inner">
        <div class="panel-head">
          <div>
            <div class="panel-title">用户增长趋势</div>
            <div class="panel-sub">近 {{ growthDays }} 天新增注册用户</div>
          </div>
          <el-select v-model="growthDays" style="width: 120px" @change="loadUserGrowth">
            <el-option :value="7" label="近7天" />
            <el-option :value="14" label="近14天" />
            <el-option :value="30" label="近30天" />
          </el-select>
        </div>
        <TrendLineChart :labels="growthLabels" :values="growthValues" />
        <div class="growth-total">
          累计新增 <strong>{{ formatNumber(growthTotal) }}</strong> 人
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Refresh, UserFilled, Reading, Document, Coin } from '@element-plus/icons-vue'
import {
  getOverview,
  getSalesStatistics,
  getSalesRank,
  getCategorySales,
  getUserGrowth
} from '@/api/adminStatistics'
import PageHeader from '@/components/admin/PageHeader.vue'
import KpiCard from '@/components/admin/KpiCard.vue'
import DonutChart from '@/components/admin/DonutChart.vue'
import TrendLineChart from '@/components/admin/TrendLineChart.vue'

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

const growthDays = ref(7)
const isRefreshing = ref(false)

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

const formatPrice = (value) => Number(value || 0).toFixed(2)
const formatNumber = (value) => Number(value || 0).toLocaleString()

const salesLabels = computed(() => salesList.value.map((item) => item.date))
const salesValues = computed(() => salesList.value.map((item) => Number(item.totalSales || 0)))
const totalSalesInRange = computed(() =>
  salesValues.value.reduce((sum, value) => sum + Number(value || 0), 0)
)

const maxSalesDay = computed(() => {
  if (!salesList.value.length) return '-'
  const top = [...salesList.value].sort(
    (a, b) => Number(b.totalSales || 0) - Number(a.totalSales || 0)
  )[0]
  return top?.date || '-'
})

const categoryDonutItems = computed(() =>
  categorySales.value
    .slice(0, 6)
    .map((item) => ({ name: item.categoryName, value: Number(item.totalQuantity || 0) }))
)

const growthLabels = computed(() => userGrowth.value.map((item) => item.date))
const growthValues = computed(() => userGrowth.value.map((item) => Number(item.userCount || 0)))
const growthTotal = computed(() =>
  growthValues.value.reduce((sum, value) => sum + Number(value || 0), 0)
)

const loadOverview = async () => {
  const res = await getOverview()
  Object.assign(overview, res.data || {})
}

const loadSales = async () => {
  const [startDate, endDate] = salesRange.value || []
  const res = await getSalesStatistics({ startDate, endDate })
  salesList.value = res.data || []
}

const loadSalesRank = async () => {
  const res = await getSalesRank({ limit: 8 })
  salesRank.value = res.data || []
}

const loadCategorySales = async () => {
  const res = await getCategorySales()
  categorySales.value = res.data || []
}

const loadUserGrowth = async () => {
  const res = await getUserGrowth({ days: growthDays.value })
  userGrowth.value = res.data || []
}

const loadAll = async () => {
  isRefreshing.value = true
  try {
    await Promise.all([
      loadOverview(),
      loadSales(),
      loadSalesRank(),
      loadCategorySales(),
      loadUserGrowth()
    ])
  } finally {
    isRefreshing.value = false
  }
}

onMounted(() => {
  loadAll()
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
  font-size: 12px;
  color: var(--admin-text-light);
  margin-bottom: 12px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.mini-stats {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.mini-stats > div {
  border: 1px solid #e6edf7;
  border-radius: 10px;
  background: #f8fbff;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mini-label {
  color: var(--admin-text-light);
  font-size: 12px;
}

.mini-stats strong {
  color: var(--admin-text-main);
  font-size: 14px;
}

.growth-total {
  margin-top: 12px;
  color: var(--admin-text-secondary);
  font-size: 13px;
}

.growth-total strong {
  color: var(--admin-text-main);
}

@media (max-width: 840px) {
  .mini-stats {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
