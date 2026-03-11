<template>
  <div class="page integration">
    <el-card shadow="never" class="create-master-card">
      <template #header>
        <div class="card-header">
          <span>总任务</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新建总任务</el-button>
        </div>
      </template>
      <p class="card-desc">按零件类型创建总任务，系统将根据所选零件自动匹配工作流（工艺审查、CAPP、CAM、CAE、结构设计等）。</p>
      <el-row :gutter="16" class="workflow-ref">
        <el-col :span="6">
          <div class="part-type-card">
            <div class="part-name">复材成型</div>
            <div class="part-flow">工艺审查 → CAPP → CAM → CAE</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="part-type-card">
            <div class="part-name">复材切边</div>
            <div class="part-flow">工艺审查 → CAPP → CAM</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="part-type-card">
            <div class="part-name">导管成形</div>
            <div class="part-flow">工艺审查 → CAPP → CAM → CAE</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="part-type-card">
            <div class="part-name">超塑成形</div>
            <div class="part-flow">结构设计 → CAPP → CAE</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    <el-card shadow="never" class="task-list">
      <template #header>
        <span>任务列表</span>
      </template>
      <el-table v-loading="loading" :data="integrationTasks" stripe style="width: 100%" row-key="id">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="work-steps-wrap">
              <div class="work-steps-title">工作步骤</div>
              <el-table :data="getWorkSteps(row.system)" size="small" class="steps-table">
                <el-table-column type="index" label="序号" width="62" />
                <el-table-column prop="name" label="步骤名称" min-width="120" />
                <el-table-column label="状态" width="100">
                  <template #default="{ row: step }">
                    <el-tag size="small" type="info">{{ step.status ?? '待执行' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="进度" width="120">
                  <template #default="{ row: step }">
                    <el-progress :percentage="step.progress ?? 0" :stroke-width="6" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default>
                    <el-button link type="primary" size="small">进入</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="任务名称" min-width="160" />
        <el-table-column prop="system" label="零件类型" width="100">
          <template #default="{ row }">
            <el-tag :type="partTagType(row.system)" size="small">{{ row.system }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="工作流" min-width="260">
          <template #default="{ row }">
            <span class="workflow-text">{{ getWorkflowLabel(row.system) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="160">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :status="row.progress === 100 ? 'success' : undefined" />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">{{ row.startTime || '—' }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">{{ row.endTime || '—' }}</template>
        </el-table-column>
        <el-table-column prop="runTimeSeconds" label="运行时间(秒)" width="120" align="right">
          <template #default="{ row }">{{ row.runTimeSeconds ?? 0 }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goDetail(row)">详情</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteTask(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" title="新建总任务" width="720px" class="task-input-dialog">
      <el-form :model="dialogForm" label-width="100px" label-position="right">
        <el-form-item label="零件类型" required>
          <el-select v-model="dialogForm.partType" placeholder="请选择零件类型" style="width: 100%" @change="onPartTypeChange">
            <el-option label="复材成型" value="复材成型" />
            <el-option label="复材切边" value="复材切边" />
            <el-option label="导管成形" value="导管成形" />
            <el-option label="超塑成形" value="超塑成形" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogForm.partType" label="工作流">
          <div class="workflow-preview">{{ getWorkflowLabel(dialogForm.partType) }}</div>
        </el-form-item>
        <el-divider content-position="left">任务输入</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" required>
              <el-input v-model="dialogForm.name" :placeholder="`如：${dialogForm.partType || '零件'}任务-001`" />
            </el-form-item>
            <el-form-item label="零件编号">
              <el-input v-model="dialogForm.partNo" placeholder="零件图号/编号" />
            </el-form-item>
            <el-form-item label="批次号">
              <el-input v-model="dialogForm.batchNo" placeholder="生产批次" />
            </el-form-item>
            <el-form-item label="材料">
              <el-input v-model="dialogForm.material" placeholder="材料牌号" />
            </el-form-item>
            <el-form-item label="规格/图号">
              <el-input v-model="dialogForm.spec" placeholder="规格或图号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数量">
              <el-input-number v-model="dialogForm.quantity" :min="1" :max="99999" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="优先级">
              <el-select v-model="dialogForm.priority" placeholder="请选择" style="width: 100%">
                <el-option label="普通" value="普通" />
                <el-option label="加急" value="加急" />
                <el-option label="紧急" value="紧急" />
              </el-select>
            </el-form-item>
            <el-form-item label="负责人">
              <el-input v-model="dialogForm.owner" placeholder="任务负责人" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="dialogForm.remark" type="textarea" :rows="2" placeholder="补充说明" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="数据模型上传">
          <el-upload
            v-model:file-list="dialogForm.modelFiles"
            class="model-upload"
            drag
            :auto-upload="false"
            :limit="5"
            accept=".step,.stp,.iges,.igs,.stl,.obj,.3ds,.pdf"
            multiple
          >
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">将 CAD/三维模型文件拖到此处，或<em>点击选择</em></div>
            <template #tip>
              <div class="upload-tip">支持 STEP(.step/.stp)、IGES(.iges/.igs)、STL、OBJ、3DS、PDF 等，单次最多 5 个文件</div>
            </template>
          </el-upload>
          <div v-if="dialogForm.modelFiles.length > 0" class="file-list">
            <div v-for="(f, i) in dialogForm.modelFiles" :key="i" class="file-item">
              <el-icon><Document /></el-icon>
              <span class="file-name">{{ f.name }}</span>
              <span class="file-size">{{ formatSize(f.size) }}</span>
              <el-button link type="danger" size="small" @click="removeModelFile(i)">移除</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitTask">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, UploadFilled, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'
import type { IntegrationTask } from '@/api/integration'
import * as integrationApi from '@/api/integration'

const PART_WORKFLOWS: Record<string, string> = {
  '复材成型': '工艺审查 → CAPP → CAM → CAE',
  '复材切边': '工艺审查 → CAPP → CAM',
  '导管成形': '工艺审查 → CAPP → CAM → CAE',
  '超塑成形': '结构设计 → CAPP → CAE',
}

const loading = ref(false)
const submitting = ref(false)
const integrationTasks = ref<IntegrationTask[]>([])
const dialogVisible = ref(false)
const dialogForm = reactive({
  partType: '' as string,
  name: '',
  partNo: '',
  batchNo: '',
  material: '',
  spec: '',
  quantity: 1,
  priority: '普通',
  owner: '',
  remark: '',
  modelFiles: [] as UploadFile[],
})

function getWorkflowLabel(partType: string) {
  return PART_WORKFLOWS[partType] || '-'
}

/** 根据零件类型解析工作流，得到工作步骤列表（二级） */
function getWorkSteps(partType: string): { name: string; status?: string; progress?: number }[] {
  const flow = PART_WORKFLOWS[partType] || '-'
  if (flow === '-') return []
  const names = flow.split(/\s*[→]\s*/).map((s) => s.trim()).filter(Boolean)
  return names.map((name) => ({ name, status: '待执行', progress: Math.floor(Math.random() * 101) }))
}

function goDetail(_row: IntegrationTask) {
  ElMessage.info('进入任务详情（可后续对接详情页）')
}

async function handleDeleteTask(row: IntegrationTask) {
  try {
    await ElMessageBox.confirm(`确定删除任务「${row.name}」？`, '提示', { type: 'warning' })
    await integrationApi.deleteIntegrationTask(row.id)
    ElMessage.success('已删除')
    fetchTasks()
  } catch (e) {
    if ((e as Error)?.message !== 'cancel') {
      ElMessage.error(e instanceof Error ? e.message : '删除失败')
    }
  }
}

function partTagType(partType: string) {
  const map: Record<string, string> = {
    '复材成型': 'primary',
    '复材切边': 'success',
    '导管成形': 'warning',
    '超塑成形': 'danger',
  }
  return map[partType] || 'info'
}

function statusTagType(s: string) {
  if (s === '已完成') return 'success'
  if (s === '进行中') return 'primary'
  return 'info'
}

async function fetchTasks() {
  loading.value = true
  try {
    const res = await integrationApi.listIntegrationTasks()
    const list = res.data ?? []
    integrationTasks.value = list.map((t) => ({
      ...t,
      progress: Math.floor(Math.random() * 101),
    }))
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载任务失败')
    integrationTasks.value = []
  } finally {
    loading.value = false
  }
}

function formatSize(size?: number) {
  if (size == null) return ''
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / (1024 * 1024)).toFixed(1)} MB`
}

function removeModelFile(index: number) {
  dialogForm.modelFiles = dialogForm.modelFiles.filter((_, i) => i !== index)
}

function openDialog() {
  dialogForm.partType = ''
  dialogForm.name = ''
  dialogForm.partNo = ''
  dialogForm.batchNo = ''
  dialogForm.material = ''
  dialogForm.spec = ''
  dialogForm.quantity = 1
  dialogForm.priority = '普通'
  dialogForm.owner = ''
  dialogForm.remark = ''
  dialogForm.modelFiles = []
  dialogVisible.value = true
}

function onPartTypeChange() {
  if (!dialogForm.name && dialogForm.partType) {
    dialogForm.name = `${dialogForm.partType}任务-${Date.now().toString().slice(-6)}`
  }
}

async function submitTask() {
  if (!dialogForm.partType) {
    ElMessage.warning('请选择零件类型')
    return
  }
  if (!dialogForm.name.trim()) {
    ElMessage.warning('请输入任务名称')
    return
  }
  submitting.value = true
  try {
    await integrationApi.createIntegrationTask({
      name: dialogForm.name.trim(),
      system: dialogForm.partType,
      status: '排队中',
      progress: 0,
    })
    ElMessage.success('总任务已创建')
    dialogVisible.value = false
    fetchTasks()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => fetchTasks())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
}
.create-master-card {
  margin-bottom: 20px;
}
.workflow-ref {
  margin-top: 8px;
}
.part-type-card {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
}
.part-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}
.part-flow {
  font-size: 12px;
  color: #606266;
}
.workflow-preview {
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
}
.workflow-text {
  font-size: 13px;
  color: #606266;
}
.task-list {
  margin-top: 0;
}
.work-steps-wrap {
  padding: 12px 20px 16px 56px;
  background: #fafbfc;
  border-bottom: 1px solid #ebeef5;
}
.work-steps-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}
.work-steps-wrap .steps-table {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}
.work-steps-wrap :deep(.el-table) {
  --el-table-border-color: #ebeef5;
}
.work-steps-wrap :deep(.el-table th),
.work-steps-wrap :deep(.el-table td) {
  padding-left: 12px;
  padding-right: 12px;
}
.task-input-dialog :deep(.el-dialog__body) {
  max-height: 70vh;
  overflow-y: auto;
}
.model-upload {
  width: 100%;
}
.model-upload :deep(.el-upload-dragger) {
  width: 100%;
}
.upload-icon {
  font-size: 40px;
  color: #409eff;
  margin-bottom: 8px;
}
.upload-text {
  font-size: 14px;
  color: #606266;
}
.upload-text em {
  color: #409eff;
  font-style: normal;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
.file-list {
  margin-top: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
  background: #fafafa;
}
.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  font-size: 13px;
}
.file-item .file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.file-item .file-size {
  color: #909399;
  font-size: 12px;
}
</style>
