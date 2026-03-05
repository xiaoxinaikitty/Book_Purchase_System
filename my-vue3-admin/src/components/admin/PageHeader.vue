<template>
  <div class="admin-page-header">
    <div>
      <h1 class="admin-page-title">{{ title }}</h1>
      <p v-if="description" class="admin-page-desc">{{ description }}</p>
    </div>
    <div class="admin-page-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.admin-page-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
}
</style>
