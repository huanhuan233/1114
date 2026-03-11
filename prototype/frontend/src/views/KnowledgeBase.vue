<template>
  <div class="page knowledge-base">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>复杂结构件制造工艺知识库软件</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增知识</el-button>
        </div>
      </template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable style="width: 140px">
            <el-option label="切削工艺" value="切削工艺" />
            <el-option label="装配工艺" value="装配工艺" />
            <el-option label="热处理" value="热处理" />
            <el-option label="钣金工艺" value="钣金工艺" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="工艺规范" value="工艺规范" />
            <el-option label="参数库" value="参数库" />
            <el-option label="模板" value="模板" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" :loading="loading" @click="fetchList">查询</el-button>
          <el-button :icon="RefreshRight" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="name" label="知识名称" min-width="180" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="updateTime" label="更新时间" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '已发布' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="10"
        :total="list.length"
        layout="total, prev, pager, next"
        class="pagination"
      />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑知识' : '新增知识'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="知识名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
            <el-option label="切削工艺" value="切削工艺" />
            <el-option label="装配工艺" value="装配工艺" />
            <el-option label="热处理" value="热处理" />
            <el-option label="钣金工艺" value="钣金工艺" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option label="工艺规范" value="工艺规范" />
            <el-option label="参数库" value="参数库" />
            <el-option label="模板" value="模板" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="form.version" placeholder="如 v1.0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="草稿" value="草稿" />
            <el-option label="已发布" value="已发布" />
          </el-select>
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
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { KnowledgeItem } from '@/api/knowledge'
import * as knowledgeApi from '@/api/knowledge'

const loading = ref(false)
const submitting = ref(false)
const list = ref<KnowledgeItem[]>([])
const searchForm = reactive({ category: '', type: '', keyword: '' })
const currentPage = ref(1)

const dialogVisible = ref(false)
const editingId = ref('')
const form = reactive({
  name: '',
  category: '切削工艺',
  type: '工艺规范',
  version: 'v1.0',
  status: '草稿',
})

async function fetchList() {
  loading.value = true
  try {
    const res = await knowledgeApi.listKnowledge({
      category: searchForm.category || undefined,
      type: searchForm.type || undefined,
      keyword: searchForm.keyword || undefined,
    })
    list.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载失败')
    list.value = []
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.category = ''
  searchForm.type = ''
  searchForm.keyword = ''
  fetchList()
}

function openDialog(row?: KnowledgeItem) {
  editingId.value = row?.id ?? ''
  form.name = row?.name ?? ''
  form.category = row?.category ?? '切削工艺'
  form.type = row?.type ?? '工艺规范'
  form.version = row?.version ?? 'v1.0'
  form.status = row?.status ?? '草稿'
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入名称')
    return
  }
  submitting.value = true
  try {
    if (editingId.value) {
      await knowledgeApi.updateKnowledge(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await knowledgeApi.createKnowledge(form)
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

async function handleDelete(row: KnowledgeItem) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', {
      type: 'warning',
    })
    await knowledgeApi.deleteKnowledge(row.id)
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
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
