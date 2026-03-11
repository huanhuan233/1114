<template>
  <div class="page cae-solver-app">
    <el-card shadow="never">
      <template #header>
        <span>渗流求解器与多物理耦合器APP</span>
      </template>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="渗流压实仿真" name="compaction">
          <div class="module-content">
            <p class="module-desc">渗流压实仿真模块，复材渗流与压实过程的多物理场仿真求解。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="渗流压实结果分析" name="compaction-result">
          <div class="module-content">
            <p class="module-desc">渗流压实结果分析模块，对渗流压实仿真结果进行后处理与分析。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="固化变形仿真" name="curing">
          <div class="module-content">
            <p class="module-desc">固化变形仿真模块，复合材料固化过程的变形与应力仿真求解。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="固化变形结果分析" name="curing-result">
          <div class="module-content">
            <p class="module-desc">固化变形结果分析模块，对固化变形仿真结果进行后处理与分析。</p>
            <el-empty description="功能待开发" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeModule = ref('compaction')
</script>

<style scoped>
.cae-solver-app {
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
