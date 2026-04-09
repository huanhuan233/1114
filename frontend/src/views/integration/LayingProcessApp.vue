<template>
  <div class="page laying-process-app">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="title-with-btn">
            <span>复材铺放工艺决策APP</span>
            <el-button size="small" type="primary">启动客户端</el-button>
          </div>
          <el-tag type="info" size="small">与 CS 端 CATIA V5 交互，平台进行结果抽取与追踪</el-tag>
        </div>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="设计模型导入与管理" name="import">
          <div class="module-content">
            <p class="module-desc">设计模型（CATIA V5）的导入、版本管理与关联。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="料片几何生成" name="geometry">
          <div class="module-content">
            <p class="module-desc">基于设计模型在 CATIA V5 中生成料片几何，平台抽取几何结果。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="料片工艺确定" name="process">
          <div class="module-content">
            <p class="module-desc">料片工艺参数与铺放策略在 CATIA 中确定，平台同步与追踪。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="铺放轨迹生成" name="trajectory">
          <div class="module-content">
            <p class="module-desc">在 CATIA V5 中生成铺放轨迹，平台进行结果抽取与仿真数据管理。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="后置处理与仿真" name="post">
          <div class="module-content">
            <p class="module-desc">CATIA 后置处理与仿真结果在平台侧汇总、展示与追踪。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
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
const activeModule = ref('import')
</script>

<style scoped>
.laying-process-app {
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
