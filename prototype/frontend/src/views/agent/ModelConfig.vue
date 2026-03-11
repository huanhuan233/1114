<template>
  <div class="page model-config">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>模型配置</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增模型</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="模型类型">
          <el-select v-model="searchType" placeholder="全部" clearable style="width: 140px">
            <el-option label="大模型" value="llm" />
            <el-option label="嵌入式模型" value="embedding" />
            <el-option label="小模型" value="small" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="模型名称/ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" :loading="loading" @click="fetchList">查询</el-button>
          <el-button :icon="RefreshRight" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="filteredModels" stripe>
        <el-table-column prop="name" label="模型名称" min-width="160" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="typeTagMap[row.type]" size="small">{{ typeLabelMap[row.type] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="provider" label="提供商" width="100" />
        <el-table-column prop="endpoint" label="API / 端点" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '可用' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="primary" size="small">测试</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑模型' : '新增模型'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="模型名称" required>
          <el-input v-model="form.name" placeholder="如：GPT-4、通义千问" />
        </el-form-item>
        <el-form-item label="模型类型" required>
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option label="大模型（LLM）" value="llm" />
            <el-option label="嵌入式模型（Embedding）" value="embedding" />
            <el-option label="小模型 / 轻量模型" value="small" />
          </el-select>
        </el-form-item>
        <el-form-item label="提供商">
          <el-input v-model="form.provider" placeholder="如：OpenAI、本地部署" />
        </el-form-item>
        <el-form-item label="API 地址">
          <el-input v-model="form.endpoint" placeholder="Base URL 或 模型 ID" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="form.apiKey" type="password" placeholder="选填，留空使用环境变量" show-password />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="用途说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ModelItem } from '@/api/models'
import * as modelApi from '@/api/models'

const typeLabelMap: Record<string, string> = {
  llm: '大模型',
  embedding: '嵌入式模型',
  small: '小模型',
}
const typeTagMap: Record<string, string> = {
  llm: 'primary',
  embedding: 'success',
  small: 'warning',
}

const loading = ref(false)
const submitting = ref(false)
const modelList = ref<ModelItem[]>([])
const searchType = ref('')
const searchKeyword = ref('')

const filteredModels = computed(() => {
  let list = modelList.value
  if (searchType.value) list = list.filter(m => m.type === searchType.value)
  if (searchKeyword.value) {
    const k = searchKeyword.value.toLowerCase()
    list = list.filter(m => (m.name && m.name.toLowerCase().includes(k)) || (m.id && m.id.includes(k)))
  }
  return list
})

const dialogVisible = ref(false)
const editingId = ref('')
const form = reactive({
  name: '',
  type: 'llm',
  provider: '',
  endpoint: '',
  apiKey: '',
  remark: '',
})

async function fetchList() {
  loading.value = true
  try {
    const res = await modelApi.listModels({
      type: searchType.value || undefined,
      keyword: searchKeyword.value || undefined,
    })
    modelList.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载失败')
    modelList.value = []
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchType.value = ''
  searchKeyword.value = ''
  fetchList()
}

function openDialog(row?: ModelItem) {
  editingId.value = row?.id ?? ''
  form.name = row?.name ?? ''
  form.type = row?.type ?? 'llm'
  form.provider = row?.provider ?? ''
  form.endpoint = row?.endpoint ?? ''
  form.apiKey = ''
  form.remark = ''
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入模型名称')
    return
  }
  submitting.value = true
  try {
    if (editingId.value) {
      await modelApi.updateModel(editingId.value, {
        name: form.name,
        type: form.type,
        provider: form.provider,
        endpoint: form.endpoint,
        apiKey: form.apiKey || undefined,
        remark: form.remark,
      })
      ElMessage.success('更新成功')
    } else {
      await modelApi.createModel({
        name: form.name,
        type: form.type,
        provider: form.provider,
        endpoint: form.endpoint,
        apiKey: form.apiKey || undefined,
        remark: form.remark,
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: ModelItem) {
  try {
    await ElMessageBox.confirm(`确定删除模型「${row.name}」？`, '提示', { type: 'warning' })
    await modelApi.deleteModel(row.id)
    ElMessage.success('已删除')
    fetchList()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e instanceof Error ? e.message : '删除失败')
  }
}

onMounted(() => fetchList())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 16px;
}
</style>
