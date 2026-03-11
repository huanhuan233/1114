<template>
  <div class="page process-app-page">
    <el-card shadow="never">
      <template #header>
        <span>{{ pageTitle }}</span>
      </template>
      <p class="placeholder-desc">本页面包含若干模块，各模块具备输入与输出配置，具体功能后续实现。</p>
      <el-empty description="模块与输入输出待开发" :image-size="100" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageTitle = computed(() => (route.meta?.title as string) || '工艺应用')
</script>

<style scoped>
.process-app-page {
  min-height: 360px;
}
.placeholder-desc {
  color: #606266;
  margin-bottom: 24px;
  font-size: 14px;
}
</style>
