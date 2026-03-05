<template>
  <div class="donut-panel">
    <div class="donut-wrap">
      <div class="donut-chart" :style="{ background: chartGradient }">
        <div class="donut-inner">
          <span class="donut-total">{{ total }}</span>
          <span class="donut-label">{{ centerLabel }}</span>
        </div>
      </div>
    </div>

    <div class="legend">
      <div v-for="item in normalizedItems" :key="item.name" class="legend-item">
        <span class="dot" :style="{ background: item.color }"></span>
        <span class="name">{{ item.name }}</span>
        <span class="value">{{ item.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  },
  centerLabel: {
    type: String,
    default: '总计'
  }
})

const palette = ['#1677ff', '#00b578', '#faad14', '#f97316', '#a855f7', '#ef4444']

const normalizedItems = computed(() =>
  props.items.map((item, idx) => ({
    name: item.name,
    value: Number(item.value || 0),
    color: item.color || palette[idx % palette.length]
  }))
)

const total = computed(() =>
  normalizedItems.value.reduce((sum, item) => sum + item.value, 0).toLocaleString()
)

const chartGradient = computed(() => {
  const list = normalizedItems.value
  const sum = list.reduce((acc, cur) => acc + cur.value, 0)
  if (!sum) {
    return 'conic-gradient(#dfe6f3 0 100%)'
  }

  let offset = 0
  const stops = list
    .map((item) => {
      const start = offset
      const end = offset + (item.value / sum) * 100
      offset = end
      return `${item.color} ${start.toFixed(2)}% ${end.toFixed(2)}%`
    })
    .join(', ')

  return `conic-gradient(${stops})`
})
</script>

<style scoped>
.donut-panel {
  display: grid;
  grid-template-columns: 190px minmax(0, 1fr);
  gap: 18px;
  align-items: center;
}

.donut-chart {
  width: 190px;
  height: 190px;
  border-radius: 50%;
  display: grid;
  place-items: center;
}

.donut-inner {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #e7edf8;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.donut-total {
  font-size: 24px;
  font-weight: 700;
  color: var(--admin-text-main);
}

.donut-label {
  font-size: 12px;
  color: var(--admin-text-secondary);
}

.legend {
  display: grid;
  gap: 10px;
}

.legend-item {
  display: grid;
  grid-template-columns: 10px minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.name {
  color: var(--admin-text-secondary);
  font-size: 13px;
}

.value {
  color: var(--admin-text-main);
  font-size: 13px;
  font-weight: 600;
}

@media (max-width: 840px) {
  .donut-panel {
    grid-template-columns: minmax(0, 1fr);
    justify-items: center;
  }

  .legend {
    width: 100%;
  }
}
</style>
