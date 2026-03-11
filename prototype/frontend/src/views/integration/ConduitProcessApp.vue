<template>
  <div class="page conduit-process-app">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>导管成形工艺仿真APP</span>
          <el-tag type="warning" size="small">与 QT 客户端交互</el-tag>
        </div>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="导管参数化建模" name="modeling">
          <div class="module-content">
            <p class="module-desc">导管参数化建模模块，通过 QT 端进行几何与参数定义，平台同步模型数据。</p>
            <el-empty description="功能待开发，与 QT 交互" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="导管材料属性编辑" name="material">
          <div class="module-content">
            <p class="module-desc">导管材料属性编辑模块，在 QT 端配置材料参数，平台进行属性管理与追踪。</p>
            <el-empty description="功能待开发，与 QT 交互" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="导管绕弯流程编辑" name="bendflow">
          <div class="module-content">
            <p class="module-desc">导管绕弯流程编辑模块，在 QT 端编排绕弯工序与工艺，平台同步流程配置。</p>
            <el-empty description="功能待开发，与 QT 交互" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="网格生成" name="mesh">
          <div class="module-content">
            <p class="module-desc">网格生成模块，QT 端完成网格划分与导出，平台进行网格数据管理与追踪。</p>
            <el-empty description="功能待开发，与 QT 交互" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="导管绕弯与回弹仿真求解" name="solve">
          <div class="module-content">
            <p class="module-desc">导管绕弯与回弹仿真求解模块，QT 端执行求解计算，平台进行结果抽取与展示。</p>
            <el-empty description="功能待开发，与 QT 交互" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="缺陷分析" name="defect">
          <div class="module-content">
            <p class="module-desc">缺陷分析模块，基于仿真结果进行缺陷识别与评估，QT 与平台协同分析。</p>
            <el-empty description="功能待开发，与 QT 交互" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeModule = ref('modeling')
</script>

<style scoped>
.conduit-process-app {
  min-height: 400px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
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
