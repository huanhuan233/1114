<template>
  <div class="process-path-embed">
    <div v-if="showHeader" class="card-header">
      <span>工艺路径生成</span>
      <el-button type="primary" :icon="Plus" @click="openDialog()">生成工艺路径</el-button>
    </div>
    <el-form :inline="true" class="search-form">
      <el-form-item label="零件名称">
        <el-input v-model="searchKeyword" placeholder="请输入" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchStatus" placeholder="全部" clearable style="width: 120px">
          <el-option label="已输出" value="已输出" />
          <el-option label="生成中" value="生成中" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" :loading="loading" @click="fetchList">查询</el-button>
        <el-button :icon="RefreshRight" @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="filteredPaths" stripe style="width: 100%">
      <el-table-column prop="partName" label="零件名称" width="160" />
      <el-table-column prop="path" label="工艺路径" min-width="380">
        <template #default="{ row }">
          <el-tag v-for="(step, i) in pathSteps(row.path)" :key="i" size="small" class="path-step">
            {{ step }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="steps" label="工序数" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === '已输出' ? 'success' : 'warning'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="outputTime" label="输出时间" width="160" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small">导出</el-button>
          <el-button link type="primary" size="small" @click="copyPath(row)">复制路径</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="10"
      :total="filteredPaths.length"
      layout="total, prev, pager, next"
      class="pagination"
    />
    <el-dialog v-model="dialogVisible" title="生成工艺路径" width="440px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="零件名称" required>
          <el-input v-model="form.partName" placeholder="请输入零件名称" />
        </el-form-item>
        <el-form-item label="工艺路径" required>
          <el-input v-model="form.path" type="textarea" :rows="3" placeholder="如：下料 → 粗车 → 精车 → 检验" />
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
import { ref, computed, reactive, onMounted, defineExpose } from 'vue'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { ProcessPathItem } from '@/api/processPath'
import * as processPathApi from '@/api/processPath'

withDefaults(defineProps<{ showHeader?: boolean }>(), { showHeader: true })
defineExpose({ openDialog })

const loading = ref(false)
const submitting = ref(false)
const pathList = ref<ProcessPathItem[]>([])
const searchKeyword = ref('')
const searchStatus = ref('')
const currentPage = ref(1)

const filteredPaths = computed(() => {
  let list = pathList.value
  if (searchKeyword.value) {
    list = list.filter(p => p.partName && p.partName.includes(searchKeyword.value))
  }
  if (searchStatus.value) {
    list = list.filter(p => p.status === searchStatus.value)
  }
  return list
})

const dialogVisible = ref(false)
const form = reactive({ partName: '', path: '' })

async function fetchList() {
  loading.value = true
  try {
    const res = await processPathApi.listProcessPath({
      partName: searchKeyword.value || undefined,
      status: searchStatus.value || undefined,
    })
    pathList.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载失败')
    pathList.value = []
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchKeyword.value = ''
  searchStatus.value = ''
  fetchList()
}

function pathSteps(path: string) {
  if (!path) return []
  return path.split(/\s*→\s*/).filter(Boolean)
}

function openDialog() {
  form.partName = ''
  form.path = ''
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.partName.trim() || !form.path.trim()) {
    ElMessage.warning('请填写零件名称和工艺路径')
    return
  }
  const steps = pathSteps(form.path).length
  submitting.value = true
  try {
    await processPathApi.createProcessPath({
      partName: form.partName,
      path: form.path,
      steps,
      status: '已输出',
    })
    ElMessage.success('已生成')
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '操作失败')
  } finally {
    submitting.value = false
  }
}

function copyPath(row: ProcessPathItem) {
  if (!row.path) return
  navigator.clipboard.writeText(row.path).then(
    () => ElMessage.success('已复制到剪贴板'),
    () => ElMessage.error('复制失败')
  )
}

onMounted(() => fetchList())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.search-form {
  margin-bottom: 16px;
}
.path-step {
  margin-right: 4px;
  margin-bottom: 4px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
