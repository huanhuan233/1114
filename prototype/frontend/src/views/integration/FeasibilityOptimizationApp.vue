<template>
  <div class="page feasibility-optimization-app">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>工艺方案可行性评估和优化APP</span>
          <el-tag type="info" size="small">可制造性分析与优化软件</el-tag>
        </div>
      </template>
      <p class="app-desc">导入工艺规程与工艺卡片，对工艺方案进行可行性评估与优化建议。</p>
      <div class="import-section">
        <div class="section-title">导入工艺输入</div>
        <p class="section-desc">上传工艺文件或粘贴工艺内容，用于后续可行性评估与优化。</p>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-card shadow="hover" class="import-card">
              <template #header>
                <span><el-icon><Upload /></el-icon> 上传文件</span>
              </template>
              <el-upload
                class="upload-area"
                drag
                :auto-upload="false"
                :limit="5"
                :on-change="handleFileChange"
                :on-exceed="handleExceed"
                accept=".txt,.doc,.docx,.pdf,.xml,.json"
              >
                <el-icon class="upload-icon"><UploadFilled /></el-icon>
                <div class="upload-text">将工艺文件拖到此处，或<em>点击选择</em></div>
                <template #tip>
                  <div class="upload-tip">支持 .txt / .doc / .docx / .pdf / .xml / .json，单次最多 5 个文件</div>
                </template>
              </el-upload>
              <div v-if="fileList.length > 0" class="file-list">
                <div v-for="(f, i) in fileList" :key="i" class="file-item">
                  <el-icon><Document /></el-icon>
                  <span class="file-name">{{ f.name }}</span>
                  <el-button link type="danger" size="small" @click="removeFile(i)">移除</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="import-card">
              <template #header>
                <span><el-icon><EditPen /></el-icon> 粘贴内容</span>
              </template>
              <el-input
                v-model="pastedContent"
                type="textarea"
                :rows="10"
                placeholder="在此粘贴工艺规程、工艺卡片等文本内容…"
                class="paste-input"
              />
              <div class="paste-actions">
                <el-button type="primary" size="small" :disabled="!pastedContent.trim()" @click="submitPaste">
                  确认导入
                </el-button>
                <el-button size="small" @click="pastedContent = ''">清空</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <el-divider />
      <div v-if="importedCount > 0" class="result-hint">
        <el-icon><CircleCheck /></el-icon>
        已导入 {{ importedCount }} 项工艺输入，可在下方进行可行性评估或优化。
      </div>
      <div class="section-title">工艺方案可行性评估与优化</div>
      <p class="section-desc">对已导入的工艺方案进行可行性评估，并给出优化建议（工序顺序、设备与资源、工时等）。</p>
      <el-empty description="导入工艺输入后在此进行可行性评估与优化" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Upload, UploadFilled, Document, EditPen, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadFile, UploadFiles } from 'element-plus'

const fileList = ref<UploadFile[]>([])
const pastedContent = ref('')
const importedCount = ref(0)

function handleFileChange(_file: UploadFile, files: UploadFiles) {
  fileList.value = files
}

function handleExceed() {
  ElMessage.warning('最多上传 5 个文件')
}

function removeFile(index: number) {
  fileList.value = fileList.value.filter((_, i) => i !== index)
}

function submitPaste() {
  if (!pastedContent.value.trim()) return
  importedCount.value += 1
  ElMessage.success('已添加粘贴内容，可继续导入或开始评估')
  pastedContent.value = ''
}
</script>

<style scoped>
.feasibility-optimization-app {
  width: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.app-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 20px;
}
.import-section {
  width: 100%;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.section-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 20px;
}
.import-card {
  margin-bottom: 0;
}
.import-card :deep(.el-card__header) {
  display: flex;
  align-items: center;
  gap: 6px;
}
.upload-area {
  width: 100%;
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
.file-list {
  margin-top: 12px;
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
}
.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  font-size: 13px;
}
.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.paste-input {
  margin-bottom: 12px;
}
.paste-input :deep(textarea) {
  font-family: inherit;
}
.paste-actions {
  display: flex;
  gap: 8px;
}
.result-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #67c23a;
  font-size: 14px;
  margin-bottom: 20px;
}
</style>
