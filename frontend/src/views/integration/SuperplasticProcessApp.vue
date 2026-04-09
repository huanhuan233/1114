<template>
  <div class="page superplastic-process-app">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="title-with-btn">
            <span>超塑成形工艺仿真APP</span>
            <el-button size="small" type="primary">启动客户端</el-button>
          </div>
          <el-tag type="success" size="small">与 Abaqus 兼容</el-tag>
        </div>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="材料本构参数" name="material">
          <div class="module-content">
            <p class="module-desc">材料本构参数模块，配置超塑成形相关材料本构模型与参数，支持 Abaqus 材料定义格式的导入与导出。</p>
            <el-empty description="功能待开发，与 Abaqus 兼容" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="工艺参数化建模" name="modeling">
          <div class="module-content">
            <p class="module-desc">工艺参数化建模模块，建立超塑成形工艺的参数化几何与工艺模型，与 Abaqus 前处理流程兼容。</p>
            <el-empty description="功能待开发，与 Abaqus 兼容" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="求解控制及后处理" name="solve">
          <div class="module-content">
            <p class="module-desc">求解控制及后处理模块，配置 Abaqus 求解参数、提交计算并完成结果后处理与可视化。</p>
            <el-empty description="功能待开发，与 Abaqus 兼容" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    <AppTaskList :app-path="route.path" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import AppTaskList from './AppTaskList.vue'

const route = useRoute()
const activeModule = ref('material')
</script>

<style scoped>
.superplastic-process-app {
  min-height: 400px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.title-with-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
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
