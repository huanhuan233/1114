<template>
  <div class="page special-process-app">
    <el-card shadow="never">
      <template #header>
        <span>特种工艺决策与指令生成APP</span>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="工艺任务管理" name="task">
          <div class="module-content">
            <p class="module-desc">工艺任务的新建、分配、跟踪与状态管理。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="工艺模板定义" name="template">
          <div class="module-content">
            <p class="module-desc">工艺模板的创建、编辑与复用配置。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="工艺方案生成" name="plan">
          <div class="module-content">
            <p class="module-desc">基于任务与模板生成工艺方案。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="工艺指令生成" name="instruction">
          <div class="module-content">
            <p class="module-desc">根据工艺方案生成可执行的工艺指令。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeModule = ref('task')
</script>

<style scoped>
.special-process-app {
  min-height: 400px;
}
.module-tabs {
  border: none;
  box-shadow: none;
}
.module-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}
.module-tabs :deep(.el-tabs__content) {
  padding: 0;
}
.module-content {
  min-height: 280px;
}
.module-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 20px;
}
</style>
