<template>
  <div class="line-chart-wrap">
    <svg viewBox="0 0 100 40" preserveAspectRatio="none" class="line-chart">
      <polyline
        :points="polylinePoints"
        fill="none"
        stroke="url(#lineGradient)"
        stroke-width="1.8"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <defs>
        <linearGradient id="lineGradient" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0%" stop-color="#1677ff" />
          <stop offset="100%" stop-color="#00b578" />
        </linearGradient>
      </defs>
    </svg>

    <div class="axis-row">
      <span>{{ firstLabel }}</span>
      <span>{{ lastLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  labels: {
    type: Array,
    default: () => []
  },
  values: {
    type: Array,
    default: () => []
  }
})

const safeValues = computed(() => {
  const mapped = props.values.map((n) => Number(n) || 0)
  return mapped.length ? mapped : [0]
})

const maxValue = computed(() => Math.max(...safeValues.value, 1))
const minValue = computed(() => Math.min(...safeValues.value, 0))
const span = computed(() => Math.max(maxValue.value - minValue.value, 1))

const polylinePoints = computed(() => {
  const list = safeValues.value
  if (list.length === 1) {
    const y = 35 - ((list[0] - minValue.value) / span.value) * 30
    return `5,${y} 95,${y}`
  }

  return list
    .map((v, i) => {
      const x = 5 + (i * 90) / (list.length - 1)
      const y = 35 - ((v - minValue.value) / span.value) * 30
      return `${x.toFixed(2)},${y.toFixed(2)}`
    })
    .join(' ')
})

const firstLabel = computed(() => props.labels[0] || '')
const lastLabel = computed(() => props.labels[props.labels.length - 1] || '')
</script>

<style scoped>
.line-chart-wrap {
  width: 100%;
}

.line-chart {
  width: 100%;
  height: 120px;
  display: block;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
  border: 1px solid #e7edf8;
  border-radius: 12px;
  padding: 4px;
}

.axis-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  color: var(--admin-text-light);
  font-size: 12px;
}
</style>
