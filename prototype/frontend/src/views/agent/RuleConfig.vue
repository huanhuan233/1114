<template>
  <div class="page rule-config-page">
    <el-card shadow="never" class="rule-card">
      <template #header>
        <div class="card-header">
          <span>规则配置</span>
          <span class="card-desc">输入自然语言描述，转换为可执行的逻辑规则（前端演示）</span>
        </div>
      </template>
      <div class="rule-layout">
        <div class="input-section">
          <div class="section-title">规则信息</div>
          <el-input v-model="ruleName" placeholder="规则名称（必填）" maxlength="120" show-word-limit />
          <el-select v-model="ruleStatus" placeholder="状态" style="width: 180px">
            <el-option label="草稿" value="草稿" />
            <el-option label="已启用" value="已启用" />
            <el-option label="已停用" value="已停用" />
          </el-select>
          <div class="section-title" style="margin-top: 8px">自然语言描述</div>
          <el-input
            v-model="naturalLanguage"
            type="textarea"
            :rows="6"
            placeholder="例如：当零件类型为「复材成型」时，先执行工艺审查，再依次执行 CAPP、CAM、CAE；若检验不通过则退回上一步。"
            maxlength="2000"
            show-word-limit
          />
          <div class="action-row">
            <el-button type="primary" :icon="RefreshRight" :loading="converting" @click="convertToLogic">
              转换为逻辑
            </el-button>
            <el-button type="success" :loading="saving" @click="saveRule">保存</el-button>
            <el-button @click="naturalLanguage = ''">清空</el-button>
          </div>
        </div>
        <div class="output-section">
          <div class="section-title">逻辑规则（输出）</div>
          <div class="logic-output">
            <pre v-if="logicOutput">{{ logicOutput }}</pre>
            <el-empty v-else description="点击「转换为逻辑」后在此显示规则" :image-size="80" />
          </div>
          <div class="hint">当前为前端演示，实际逻辑解析与执行需对接后端/NLP 服务。</div>
        </div>
      </div>

      <div class="rule-list">
        <div class="list-header">
          <div class="section-title">规则列表</div>
          <div class="list-actions">
            <el-input v-model="keyword" placeholder="搜索名称/内容" clearable size="small" style="width: 220px" />
            <el-button size="small" @click="fetchList">刷新</el-button>
          </div>
        </div>
        <el-table v-loading="listLoading" :data="filteredRules" stripe size="small">
          <el-table-column prop="name" label="规则名称" min-width="160" />
          <el-table-column prop="status" label="状态" width="90" />
          <el-table-column prop="updateTime" label="更新时间" width="160" />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="editRule(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="removeRule(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RefreshRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as ruleApi from '@/api/rules'

const naturalLanguage = ref('')
const logicOutput = ref('')
const converting = ref(false)
const saving = ref(false)

const ruleId = ref<string | null>(null)
const ruleName = ref('')
const ruleStatus = ref<'草稿' | '已启用' | '已停用'>('草稿')

const listLoading = ref(false)
const rules = ref<ruleApi.RuleItem[]>([])
const keyword = ref('')

function convertToLogic() {
  if (!naturalLanguage.value.trim()) {
    ElMessage.warning('请先输入自然语言描述')
    return
  }
  converting.value = true
  // 前端演示：简单模拟转换结果
  setTimeout(() => {
    logicOutput.value = `# 解析结果（示例）
type: workflow_rule
conditions:
  - key: part_type
    value: "复材成型"
steps:
  - id: step_1
    name: 工艺审查
    next: step_2
  - id: step_2
    name: CAPP
    next: step_3
  - id: step_3
    name: CAM
    next: step_4
  - id: step_4
    name: CAE
    next: end
  - id: end
    name: 结束
fallback:
  on_fail: 退回上一步
  retry: 1
`
    converting.value = false
    ElMessage.success('已生成逻辑（演示数据）')
  }, 800)
}

async function fetchList() {
  listLoading.value = true
  try {
    const res = await ruleApi.listRules()
    rules.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载规则列表失败')
  } finally {
    listLoading.value = false
  }
}

const filteredRules = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return rules.value
  return rules.value.filter(r =>
    r.name.toLowerCase().includes(kw)
    || r.naturalLanguage.toLowerCase().includes(kw)
    || r.logicOutput.toLowerCase().includes(kw),
  )
})

async function saveRule() {
  if (!ruleName.value.trim()) {
    ElMessage.warning('请填写规则名称')
    return
  }
  saving.value = true
  try {
    const body = {
      name: ruleName.value,
      status: ruleStatus.value,
      naturalLanguage: naturalLanguage.value,
      logicOutput: logicOutput.value,
    }
    if (ruleId.value) {
      await ruleApi.updateRule(ruleId.value, body)
      ElMessage.success('已保存')
    } else {
      const res = await ruleApi.createRule(body)
      ruleId.value = res.data?.id ?? null
      ElMessage.success('已新增并保存')
    }
    fetchList()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    saving.value = false
  }
}

function editRule(row: ruleApi.RuleItem) {
  ruleId.value = row.id
  ruleName.value = row.name
  ruleStatus.value = (row.status as any) ?? '草稿'
  naturalLanguage.value = row.naturalLanguage
  logicOutput.value = row.logicOutput
  ElMessage.success('已回填到编辑区')
}

function removeRule(id: string) {
  ElMessageBox.confirm('确定删除该规则？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await ruleApi.deleteRule(id)
      if (ruleId.value === id) ruleId.value = null
      ElMessage.success('已删除')
      fetchList()
    } catch (e) {
      ElMessage.error(e instanceof Error ? e.message : '删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.rule-config-page {
  height: 100%;
  min-height: 400px;
}
.rule-card {
  height: 100%;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.card-desc {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}
.rule-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  min-height: 360px;
}
@media (max-width: 900px) {
  .rule-layout {
    grid-template-columns: 1fr;
  }
}
.input-section,
.output-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.section-title {
  font-weight: 600;
  color: #303133;
}
.action-row {
  display: flex;
  gap: 8px;
}
.logic-output {
  flex: 1;
  min-height: 200px;
  padding: 12px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: auto;
}
.logic-output pre {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-all;
}
.hint {
  font-size: 12px;
  color: #909399;
}

.rule-list {
  margin-top: 20px;
}
.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.list-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
