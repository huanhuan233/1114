<template>
  <div class="page filament-process-app">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="title-with-btn">
            <span>复材铺丝工艺编程APP</span>
            <el-button size="small" type="primary">启动客户端</el-button>
          </div>
          <el-tag type="info" size="small">与 CS 端 CATIA V5 交互，平台进行结果抽取与追踪</el-tag>
        </div>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="铺层工艺模型管理" name="layer">
          <div class="module-content">
            <p class="module-desc">铺层工艺模型在 CATIA V5 中的管理与版本控制，平台同步与追踪。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="丝束几何规划" name="geometry">
          <div class="module-content">
            <p class="module-desc">在 CATIA V5 中进行丝束几何规划，平台抽取规划结果。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="丝束可铺放分析" name="analysis">
          <div class="module-content">
            <p class="module-desc">丝束可铺放性在 CATIA 中分析，平台进行结果抽取与追踪。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="丝束编辑" name="edit">
          <div class="module-content">
            <p class="module-desc">丝束路径与参数在 CATIA V5 中编辑，平台同步编辑结果。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="丝束铺放编程" name="programming">
          <div class="module-content">
            <p class="module-desc">在 CATIA V5 中完成丝束铺放编程，平台抽取编程结果与指令。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="构件切边编程" name="trimming">
          <div class="module-content">
            <p class="module-desc">构件切边编程在 CATIA 中完成，平台进行结果抽取与追踪。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="铺丝设备后置处理" name="post">
          <div class="module-content">
            <p class="module-desc">铺丝设备后置处理在 CATIA 中执行，平台汇总与追踪后置结果。</p>
            <el-empty description="CATIA 计算完成后由平台抽取与追踪结果" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="铺放过程仿真" name="simulation">
          <div class="module-content">
            <p class="module-desc">铺放过程仿真在 CATIA V5 中运行，平台进行仿真结果抽取与展示。</p>
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
const activeModule = ref('layer')
</script>

<style scoped>
.filament-process-app {
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
