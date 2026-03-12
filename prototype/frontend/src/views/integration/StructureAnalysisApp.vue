<template>
  <div class="page structure-analysis-app">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>结构合理性分析APP</span>
          <el-tag type="info" size="small">可制造性分析与优化软件</el-tag>
        </div>
      </template>
      <p class="app-desc">对产品几何与结构进行可制造性结构合理性分析，检查设计约束、工艺约束与结构可行性。</p>
      <el-tabs v-model="activeModule" type="border-card" class="module-tabs">
        <el-tab-pane label="结构/几何输入" name="input">
          <div class="module-content">
            <p class="module-desc">导入产品模型或结构数据（CAD 几何、特征树等），作为结构合理性分析输入。</p>
            <el-upload
              class="upload-area"
              drag
              :auto-upload="false"
              :limit="3"
              accept=".step,.stp,.iges,.igs,.xml,.json"
            >
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="upload-text">将结构/几何文件拖到此处，或<em>点击选择</em></div>
              <template #tip>
                <div class="upload-tip">支持 .step / .stp / .iges / .igs / .xml / .json</div>
              </template>
            </el-upload>
          </div>
        </el-tab-pane>
        <el-tab-pane label="结构合理性检查" name="check">
          <div class="module-content">
            <p class="module-desc">基于工艺约束与设计规范，对结构可制造性进行检查（如可加工性、可装配性、壁厚与圆角等）。</p>
            <el-empty description="选择输入后进行结构合理性检查" :image-size="80" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="分析报告" name="report">
          <div class="module-content">
            <p class="module-desc">查看结构合理性分析报告与改进建议。</p>
            <el-empty description="完成检查后可查看分析报告" :image-size="80" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'

const activeModule = ref('input')
</script>

<style scoped>
.structure-analysis-app {
  min-height: 400px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.app-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 20px;
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
.upload-area {
  width: 100%;
  max-width: 560px;
}
.upload-area :deep(.el-upload-dragger) {
  width: 100%;
}
.upload-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 12px;
}
.upload-text {
  color: #606266;
  font-size: 14px;
}
.upload-text em {
  color: #409eff;
  font-style: normal;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
