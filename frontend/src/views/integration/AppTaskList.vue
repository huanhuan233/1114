<template>
  <el-card shadow="never" class="app-task-list-card">
    <template #header>
      <div class="card-header-row">
        <span>涉及本节点的任务</span>
        <div class="header-actions">
          <span v-if="appPath" class="filter-path" :title="appPath">筛选: {{ appPath }}</span>
          <el-button type="primary" link size="small" :loading="loading" @click="fetchTasks">刷新</el-button>
        </div>
      </div>
    </template>
    <p v-if="appPath" class="card-desc">以下任务的工作流中包含本节点（按当前节点路径从后端拉取），发布后在此展示。</p>
    <el-table
      v-loading="loading"
      :data="tasks"
      stripe
      size="small"
      max-height="280"
      style="width: 100%"
    >
      <el-table-column prop="name" label="任务名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="system" label="零件类型" width="90">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.system || '—' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="statusTagType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度" width="100">
        <template #default="{ row }">
          <el-progress :percentage="row.progress ?? 0" :stroke-width="6" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.appId"
            link
            type="primary"
            size="small"
            @click="goWorkflow(row)"
          >
            进入工作流
          </el-button>
          <el-button link type="primary" size="small" @click="goTaskList">任务管理</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty
      v-if="!loading && appPath && tasks.length === 0"
      description="暂无涉及本节点的任务，在任务管理新建并发布后会出现"
      :image-size="64"
    />
    <p v-if="!appPath" class="card-desc">未指定节点路径，无法筛选任务。</p>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { IntegrationTask } from '@/api/integration'
import * as integrationApi from '@/api/integration'

const props = defineProps<{
  /** 当前 APP 对应的工作流节点 appPath，如 /integration/process-review/feasibility */
  appPath: string
}>()

const router = useRouter()
const loading = ref(false)
const tasks = ref<IntegrationTask[]>([])

function statusTagType(status: string) {
  if (status === '排队中' || status === '执行中') return 'warning'
  if (status === '已完成') return 'success'
  if (status === '待配置') return 'info'
  return 'info'
}

function goWorkflow(row: IntegrationTask) {
  if (row.appId) router.push(`/agent-config/workflow?appId=${row.appId}`)
}

function goTaskList() {
  router.push('/integration')
}

async function fetchTasks() {
  const path = props.appPath?.trim()
  if (!path) {
    tasks.value = []
    return
  }
  loading.value = true
  try {
    const res = await integrationApi.listIntegrationTasks({ appPath: path })
    tasks.value = Array.isArray(res.data) ? res.data : []
  } catch {
    tasks.value = []
  } finally {
    loading.value = false
  }
}

watch(() => props.appPath, fetchTasks, { immediate: true })
onMounted(fetchTasks)
</script>

<style scoped>
.app-task-list-card {
  margin-top: 20px;
}
.app-task-list-card :deep(.el-card__header) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}
.card-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-path {
  font-size: 12px;
  color: #909399;
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px 0;
}
</style>
