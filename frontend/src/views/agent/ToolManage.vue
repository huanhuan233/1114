<template>
  <div class="page tool-manage-page">
    <el-card shadow="never" class="tool-card">
      <template #header>
        <div class="card-header">
          <span>工具管理</span>
          <el-button type="primary" size="small" :icon="Plus" @click="openAddDialog">添加工具</el-button>
        </div>
        <p class="card-desc">管理智能体可调用的工具，包括 MCP（Model Context Protocol）等。</p>
      </template>
      <div class="tool-tabs">
        <el-radio-group v-model="activeCategory" size="small" @change="fetchList">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="mcp">MCP</el-radio-button>
          <el-radio-button value="api">API 工具</el-radio-button>
          <el-radio-button value="custom">自定义</el-radio-button>
        </el-radio-group>
      </div>
      <el-table v-loading="loading" :data="filteredTools" stripe class="tool-table">
        <el-table-column prop="name" label="工具名称" min-width="160" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'mcp' ? 'primary' : row.type === 'api' ? 'success' : 'info'" size="small">
              {{ typeLabel[row.type] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="endpoint" label="端点 / 配置" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '已启用' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="desc" label="说明" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small">测试</el-button>
            <el-button link type="danger" size="small" @click="removeTool(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑工具' : '添加工具'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="工具名称" required>
          <el-input v-model="form.name" placeholder="如：文件读取 MCP" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option label="MCP（Model Context Protocol）" value="mcp" />
            <el-option label="API 工具" value="api" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="端点/地址">
          <el-input v-model="form.endpoint" placeholder="MCP 服务地址或 API URL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="已启用">已启用</el-radio>
            <el-radio value="已停用">已停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.desc" type="textarea" :rows="2" placeholder="工具用途说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTool">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as toolApi from '@/api/tools'

const typeLabel: Record<string, string> = {
  mcp: 'MCP',
  api: 'API 工具',
  custom: '自定义',
}

const loading = ref(false)
const activeCategory = ref('all')
const dialogVisible = ref(false)
const editingId = ref<string | null>(null)

const tools = ref<toolApi.ToolItem[]>([])

async function fetchList() {
  loading.value = true
  try {
    const type = activeCategory.value === 'all' ? undefined : activeCategory.value
    const res = await toolApi.listTools({ type })
    tools.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const filteredTools = computed(() => {
  if (activeCategory.value === 'all') return tools.value
  return tools.value.filter(t => t.type === activeCategory.value)
})

const form = ref({
  name: '',
  type: 'mcp',
  endpoint: '',
  status: '已启用',
  desc: '',
})

function openAddDialog() {
  editingId.value = null
  form.value = { name: '', type: 'mcp', endpoint: '', status: '已启用', desc: '' }
  dialogVisible.value = true
}

function openEdit(row: ToolItem) {
  editingId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

async function submitTool() {
  if (!form.value.name.trim()) {
    ElMessage.warning('请填写工具名称')
    return
  }
  loading.value = true
  try {
    if (editingId.value) {
      await toolApi.updateTool(editingId.value, form.value)
      ElMessage.success('已更新')
    } else {
      await toolApi.createTool(form.value)
      ElMessage.success('已添加')
    }
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    loading.value = false
  }
}

function removeTool(id: string) {
  ElMessageBox.confirm('确定删除该工具？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    loading.value = true
    try {
      await toolApi.deleteTool(id)
      ElMessage.success('已删除')
      fetchList()
    } catch (e) {
      ElMessage.error(e instanceof Error ? e.message : '删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.tool-manage-page {
  height: 100%;
}
.tool-card {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-desc {
  margin: 8px 0 0;
  font-size: 12px;
  color: #909399;
}
.tool-tabs {
  margin-bottom: 16px;
}
.tool-table {
  margin-top: 0;
}
</style>
