<template>
  <div class="kpi-card">
    <div class="kpi-icon" :style="{ background: iconBg }">
      <el-icon :size="22">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="kpi-main">
      <div class="kpi-label">{{ label }}</div>
      <div class="kpi-value">{{ value }}</div>
      <div class="kpi-foot">{{ footnote }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: [Object, Function],
    required: true
  },
  iconBg: {
    type: String,
    default: 'linear-gradient(135deg, #0ea5e9, #2563eb)'
  },
  label: {
    type: String,
    required: true
  },
  value: {
    type: String,
    required: true
  },
  footnote: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.kpi-card {
  border: 1px solid var(--admin-border);
  background: var(--admin-panel);
  border-radius: var(--admin-radius-lg);
  box-shadow: var(--admin-shadow);
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.kpi-icon {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  color: #fff;
  display: grid;
  place-items: center;
}

.kpi-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.kpi-label {
  color: var(--admin-text-secondary);
  font-size: 13px;
}

.kpi-value {
  color: var(--admin-text-main);
  font-size: 25px;
  font-weight: 700;
  letter-spacing: 0.4px;
  line-height: 1.2;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.kpi-foot {
  color: var(--admin-text-light);
  font-size: 12px;
}
</style>
