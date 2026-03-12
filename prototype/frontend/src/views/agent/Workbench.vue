<template>
  <div class="workbench-page">
    <!-- 顶栏：我创建的、全部标签、搜索 -->
    <header class="workbench-header">
      <div class="header-left">
        <el-checkbox v-model="myCreationsOnly">我创建的</el-checkbox>
        <el-select v-model="selectedTag" placeholder="全部标签" clearable class="tag-select">
          <el-option label="全部标签" value="" />
          <el-option v-for="t in tags" :key="t" :label="t" :value="t" />
        </el-select>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索"
          clearable
          class="search-input"
          :prefix-icon="Search"
        />
      </div>
    </header>

    <!-- 分类 Tab：全部、工作流、Chatflow、聊天助手、Agent、文本生成 -->
    <div class="category-tabs">
      <button
        v-for="cat in categories"
        :key="cat.value"
        class="tab-btn"
        :class="{ active: activeCategory === cat.value }"
        @click="activeCategory = cat.value"
      >
        {{ cat.label }}
      </button>
    </div>

    <!-- 主内容：左侧创建应用 + 右侧应用卡片网格 -->
    <div class="workbench-main">
      <aside class="create-panel">
        <div class="create-card">
          <h3 class="create-title">创建应用</h3>
          <p class="create-hint">新建任务并自动生成工作流请前往「任务管理」</p>
          <div class="create-options">
            <div class="create-option" @click="createBlank">
              <span class="option-icon"><el-icon><DocumentAdd /></el-icon></span>
              <span>创建空白应用</span>
            </div>
            <div class="create-option" @click="createFromTemplate">
              <span class="option-icon"><el-icon><FolderOpened /></el-icon></span>
              <span>从应用模板创建</span>
            </div>
            <div class="create-option" @click="importDsl">
              <span class="option-icon"><el-icon><Upload /></el-icon></span>
              <span>导入 DSL 文件</span>
            </div>
          </div>
        </div>
      </aside>
      <section class="apps-grid" v-loading="loading">
        <div
          v-for="app in filteredApps"
          :key="app.id"
          class="app-card"
          @click="openApp(app)"
        >
          <div class="app-icon">
            <el-icon :size="32"><MagicStick /></el-icon>
          </div>
          <div class="app-title">{{ app.name }}</div>
          <div class="app-badges">
            <el-tag size="small" :type="app.status === '已发布' ? 'success' : 'info'">{{ app.status || '草稿' }}</el-tag>
            <el-tag size="small" type="primary" effect="plain">{{ typeLabel(app.type) }}</el-tag>
          </div>
          <div class="app-meta">编辑于 {{ app.updateTime || '-' }}</div>
          <div v-if="app.desc" class="app-desc">{{ app.desc }}</div>
          <div class="app-footer">
            <el-button link size="small" type="primary" @click.stop="addTag(app)">
              <el-icon><CollectionTag /></el-icon>
              添加标签
            </el-button>
            <el-button link size="small" type="danger" @click.stop="handleDeleteApp(app)">
              删除
            </el-button>
          </div>
        </div>
      </section>
    </div>

    <!-- 底部：拖拽 DSL 提示 -->
    <footer class="workbench-footer">
      <span class="footer-hint">拖拽 DSL 文件到此处创建应用</span>
    </footer>
  </div>

  <el-dialog v-model="createDialogVisible" title="创建空白应用" width="520px">
    <el-form :model="createForm" label-width="90px">
      <el-form-item label="应用名称" required>
        <el-input v-model="createForm.name" placeholder="如：SVG生成、工艺工作流" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="createForm.desc" type="textarea" :rows="2" placeholder="可选" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="createForm.type" style="width: 100%">
          <el-option label="Chatflow" value="chatflow" />
          <el-option label="工作流" value="workflow" />
          <el-option label="Agent" value="agent" />
          <el-option label="聊天助手" value="chat" />
          <el-option label="文本生成" value="text" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="creating" @click="submitCreate">创建并进入</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, DocumentAdd, FolderOpened, Upload, MagicStick, CollectionTag } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as appApi from '@/api/apps'

const router = useRouter()
const myCreationsOnly = ref(false)
const selectedTag = ref('')
const searchKeyword = ref('')
const activeCategory = ref('all')

const categories = [
  { label: '全部', value: 'all' },
  { label: '工作流', value: 'workflow' },
  { label: 'Chatflow', value: 'chatflow' },
  { label: '聊天助手', value: 'chat' },
  { label: 'Agent', value: 'agent' },
  { label: '文本生成', value: 'text' },
]

const tags = ref(['工艺', '问答', '生成', '测试'])

const apps = ref<appApi.AppItem[]>([])
const loading = ref(false)

const filteredApps = computed(() => {
  let list = apps.value
  if (activeCategory.value !== 'all') {
    list = list.filter(a => a.type === activeCategory.value)
  }
  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(a => a.name.toLowerCase().includes(kw) || (a.desc && a.desc.toLowerCase().includes(kw)))
  }
  return list
})

async function fetchApps() {
  loading.value = true
  try {
    const res = await appApi.listApps()
    apps.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载应用失败')
  } finally {
    loading.value = false
  }
}

const createDialogVisible = ref(false)
const creating = ref(false)
const createForm = ref({ name: '', desc: '', type: 'chatflow' })

function createBlank() {
  createForm.value = { name: '', desc: '', type: 'chatflow' }
  createDialogVisible.value = true
}

function createFromTemplate() {
  ElMessage.info('从应用模板创建（前端演示）')
}

function importDsl() {
  ElMessage.info('导入 DSL 文件（前端演示）')
}

function openApp(app: appApi.AppItem) {
  router.push(`/agent-config/workflow?appId=${app.id}`)
}

function typeLabel(type: string) {
  const map: Record<string, string> = {
    workflow: '工作流',
    chatflow: 'Chatflow',
    agent: 'Agent',
    chat: '聊天助手',
    text: '文本生成',
  }
  return map[type] || type || '应用'
}

function addTag(app: appApi.AppItem) {
  ElMessage.info(`为「${app.name}」添加标签（后续实现）`)
}

async function handleDeleteApp(app: appApi.AppItem) {
  try {
    await ElMessageBox.confirm(`确定删除应用「${app.name}」？删除后不可恢复。`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    await appApi.deleteApp(app.id)
    ElMessage.success('已删除')
    fetchApps()
  } catch (e) {
    if ((e as Error)?.message !== 'cancel') {
      ElMessage.error(e instanceof Error ? e.message : '删除失败')
    }
  }
}

async function submitCreate() {
  if (!createForm.value.name.trim()) {
    ElMessage.warning('请填写应用名称')
    return
  }
  creating.value = true
  try {
    const res = await appApi.createApp({
      name: createForm.value.name,
      desc: createForm.value.desc,
      type: createForm.value.type,
      workflowJson: '',
    })
    createDialogVisible.value = false
    const id = res.data?.id
    router.push(`/agent-config/workflow?appId=${id}`)
    ElMessage.success('已创建')
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '创建失败')
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  fetchApps()
})
</script>

<style scoped>
.workbench-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f6f8;
}
.workbench-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.tag-select {
  width: 140px;
}
.search-input {
  width: 220px;
}
.category-tabs {
  padding: 12px 24px 0;
  background: #fff;
  display: flex;
  gap: 4px;
  border-bottom: 1px solid #e5e7eb;
}
.tab-btn {
  padding: 10px 16px;
  border: none;
  background: transparent;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  border-radius: 6px;
}
.tab-btn:hover {
  color: #111827;
  background: #f3f4f6;
}
.tab-btn.active {
  color: #2563eb;
  background: #eff6ff;
  font-weight: 500;
}
.workbench-main {
  flex: 1;
  display: flex;
  gap: 24px;
  padding: 24px;
  min-height: 0;
}
.create-panel {
  flex-shrink: 0;
  width: 280px;
}
.create-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
}
.create-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}
.create-hint {
  margin: 0 0 16px;
  font-size: 12px;
  color: #6b7280;
}
.create-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.create-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
  transition: border-color 0.2s, background 0.2s;
}
.create-option:hover {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}
.option-icon {
  font-size: 20px;
  color: #6b7280;
}
.create-option:hover .option-icon {
  color: #2563eb;
}
.apps-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  align-content: start;
  overflow: auto;
}
.app-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
  min-height: 160px;
}
.app-card:hover {
  border-color: #2563eb;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
}
.app-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: #eff6ff;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}
.app-title {
  font-weight: 600;
  font-size: 15px;
  color: #111827;
  margin-bottom: 8px;
}
.app-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 6px;
}
.app-badges .el-tag {
  margin: 0;
}
.app-meta {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
}
.app-desc {
  font-size: 12px;
  color: #9ca3af;
  flex: 1;
  margin-bottom: 8px;
}
.app-footer {
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 8px;
}
.workbench-footer {
  padding: 16px;
  text-align: center;
  background: #fff;
  border-top: 1px dashed #e5e7eb;
}
.footer-hint {
  font-size: 12px;
  color: #9ca3af;
}
</style>
