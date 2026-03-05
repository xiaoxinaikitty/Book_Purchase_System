<template>
  <section class="admin-page">
    <PageHeader title="数据统计" description="洞察销售、品类贡献和用户增长趋势">
      <template #actions>
        <div class="admin-filter-bar">
          <el-date-picker
            v-model="salesRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadSales"
          />
          <el-select v-model="growthDays" style="width: 120px" @change="loadUserGrowth">
            <el-option :value="7" label="近7天" />
            <el-option :value="14" label="近14天" />
            <el-option :value="30" label="近30天" />
          </el-select>
          <el-button type="primary" :icon="Refresh" :loading="refreshing" @click="loadAll">
            刷新
          </el-button>
        </div>
      </template>
    </PageHeader>

    <div class="admin-grid-4">
      <KpiCard
        :icon="UserFilled"
        icon-bg="linear-gradient(135deg, #1677ff, #14b8a6)"
        label="用户总数"
        :value="formatNumber(overview.userCount)"
        :footnote="`今日 +${formatNumber(overview.todayUserCount)}`"
      />
      <KpiCard
        :icon="Reading"
        icon-bg="linear-gradient(135deg, #6366f1, #a855f7)"
        label="图书总量"
        :value="formatNumber(overview.bookCount)"
        footnote="全站在售数据"
      />
      <KpiCard
        :icon="Document"
        icon-bg="linear-gradient(135deg, #f97316, #ef4444)"
        label="订单总数"
        :value="formatNumber(overview.orderCount)"
        :footnote="`今日 +${formatNumber(overview.todayOrderCount)}`"
      />
      <KpiCard
        :icon="Coin"
        icon-bg="linear-gradient(135deg, #00b578, #16a34a)"
        label="销售总额"
        :value="`¥${formatPrice(overview.totalSales)}`"
        :footnote="`今日 ¥${formatPrice(overview.todaySales)}`"
      />
    </div>

    <div class="admin-grid-2">
      <div class="admin-card admin-card-inner">
        <div class="panel-title">销售趋势折线</div>
        <div class="panel-sub">统计区间内每日销售额变化</div>
        <TrendLineChart :labels="salesLabels" :values="salesValues" />
      </div>
      <div class="admin-card admin-card-inner">
        <div class="panel-title">分类销售占比</div>
        <div class="panel-sub">按销量汇总前 6 个分类（圆环图）</div>
        <DonutChart :items="categoryItems" center-label="销量" />
      </div>
    </div>

    <div class="admin-grid-2">
      <div class="admin-card admin-card-inner">
        <div class="panel-head">
          <div>
            <div class="panel-title">图书销量排行</div>
            <div class="panel-sub">按销量降序展示 Top 10</div>
          </div>
          <el-tag type="info" effect="plain">Top {{ salesRank.length }}</el-tag>
        </div>
        <el-table :data="salesRank" stripe size="small">
          <el-table-column type="index" width="54" label="#" />
          <el-table-column prop="title" label="图书" min-width="160" />
          <el-table-column prop="sales" label="销量" width="90" />
          <el-table-column label="销售额" width="120">
            <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <div class="admin-card admin-card-inner">
        <div class="panel-head">
          <div>
            <div class="panel-title">用户增长明细</div>
            <div class="panel-sub">近 {{ growthDays }} 天注册人数</div>
          </div>
          <el-tag type="success" effect="plain">累计 {{ growthTotal }}</el-tag>
        </div>
        <el-table :data="userGrowth" stripe size="small">
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="userCount" label="新增用户" width="110" />
        </el-table>
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
const refreshing = ref(false)

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

const formatPrice = (price) => Number(price || 0).toFixed(2)
const formatNumber = (value) => Number(value || 0).toLocaleString()

const salesLabels = computed(() => salesList.value.map((item) => item.date))
const salesValues = computed(() => salesList.value.map((item) => Number(item.totalSales || 0)))
const growthTotal = computed(() =>
  userGrowth.value.reduce((sum, item) => sum + Number(item.userCount || 0), 0)
)

const categoryItems = computed(() =>
  categorySales.value
    .slice(0, 6)
    .map((item) => ({ name: item.categoryName, value: Number(item.totalQuantity || 0) }))
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
  const res = await getSalesRank({ limit: 10 })
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
  refreshing.value = true
  try {
    await Promise.all([
      loadOverview(),
      loadSales(),
      loadSalesRank(),
      loadCategorySales(),
      loadUserGrowth()
    ])
  } finally {
    refreshing.value = false
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
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}
</style>
