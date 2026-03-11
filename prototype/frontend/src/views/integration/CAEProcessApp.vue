<template>
  <div class="page cae-process-app">
    <el-card shadow="never">
      <template #header>
        <span>复材制造工艺过程仿真APP</span>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="铺放工艺过程" name="laying">
          <div class="module-content">
            <p class="module-desc">铺放工艺过程模块，复材铺放过程的工艺仿真与分析。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="固化工艺过程仿真" name="curing">
          <div class="module-content">
            <p class="module-desc">固化工艺过程仿真模块，复合材料固化过程的温度场、应力与变形仿真。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="铺放-固化工艺联合仿真" name="joint">
          <div class="module-content">
            <p class="module-desc">铺放-固化工艺联合仿真模块，铺放与固化工艺的耦合仿真与一体化分析。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="基于代理模型固化变形快速预测" name="surrogate">
          <div class="module-content">
            <p class="module-desc">基于代理模型固化变形快速预测模块，通过代理模型实现固化变形的快速预测与优化。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeModule = ref('laying')
</script>

<style scoped>
.cae-process-app {
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
