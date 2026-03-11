<template>
  <div class="page capp-page">
    <div class="capp-layout">
      <!-- 左侧：工艺规程/产品树 -->
      <div class="panel left-panel">
        <div class="panel-header">
          <span>工艺规程</span>
          <el-button type="primary" size="small" :icon="Plus">新建规程</el-button>
        </div>
        <el-input v-model="treeFilter" placeholder="搜索规程" clearable size="small" class="tree-filter">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-tree
          :data="processTree"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          highlight-current
          default-expand-all
          class="process-tree"
          @node-click="onTreeSelect"
        >
          <template #default="{ node }">
            <span class="tree-node">
              <el-icon v-if="node.level === 1"><Document /></el-icon>
              <el-icon v-else><Folder /></el-icon>
              <span>{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
      <!-- 中间：工艺路线 / 工艺卡片 -->
      <div class="panel center-panel">
        <el-tabs v-model="activeTab" class="center-tabs">
          <el-tab-pane label="工艺路线" name="route">
            <div class="toolbar">
              <el-button size="small" :icon="Plus">添加工序</el-button>
              <el-button size="small" :icon="Sort">调整顺序</el-button>
              <el-button size="small" type="primary" :icon="Download">导出路线</el-button>
            </div>
            <el-table :data="routeList" stripe size="small" max-height="320">
              <el-table-column type="index" label="序号" width="78" />
              <el-table-column prop="code" label="工序号" width="88" />
              <el-table-column prop="name" label="工序名称" min-width="120" />
              <el-table-column prop="workcenter" label="工作中心" width="100" />
              <el-table-column prop="equipment" label="设备" width="100" />
              <el-table-column prop="time" label="定额(h)" width="90" align="right" />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default>
                  <el-button link type="primary" size="small">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="工艺卡片" name="cards">
            <div class="toolbar">
              <el-button size="small" :icon="Plus">新建卡片</el-button>
              <el-select v-model="cardType" size="small" style="width: 140px" placeholder="卡片类型">
                <el-option label="机械加工工序卡" value="machining" />
                <el-option label="装配工序卡" value="assembly" />
                <el-option label="检验卡" value="inspection" />
              </el-select>
            </div>
            <el-table :data="cardList" stripe size="small" max-height="320">
              <el-table-column prop="cardNo" label="卡片编号" width="120" />
              <el-table-column prop="title" label="卡片名称" min-width="160" />
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="version" label="版本" width="70" />
              <el-table-column label="操作" width="120" fixed="right">
                <template #default>
                  <el-button link type="primary" size="small">查看</el-button>
                  <el-button link type="primary" size="small">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
      <!-- 右侧：工序属性 / 资源 -->
      <div class="panel right-panel">
        <div class="panel-header">工序属性</div>
        <template v-if="selectedNode">
          <el-descriptions :column="1" border size="small" class="props-desc">
            <el-descriptions-item label="工序号">{{ selectedNode.code || '-' }}</el-descriptions-item>
            <el-descriptions-item label="工序名称">{{ selectedNode.name }}</el-descriptions-item>
            <el-descriptions-item label="工作中心">{{ selectedNode.workcenter || '-' }}</el-descriptions-item>
            <el-descriptions-item label="设备">{{ selectedNode.equipment || '-' }}</el-descriptions-item>
            <el-descriptions-item label="工时定额">{{ selectedNode.time ?? '-' }} h</el-descriptions-item>
          </el-descriptions>
          <el-divider />
          <div class="panel-header sub">关联资源</div>
          <el-tag v-for="r in selectedNode.resources" :key="r" size="small" class="res-tag">{{ r }}</el-tag>
          <el-empty v-if="!selectedNode.resources?.length" description="暂无" :image-size="48" />
        </template>
        <el-empty v-else description="在左侧选择工序" :image-size="64" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Plus, Search, Sort, Download } from '@element-plus/icons-vue'

const treeFilter = ref('')
const activeTab = ref('route')
const cardType = ref('machining')

interface RouteItem {
  code: string
  name: string
  workcenter: string
  equipment: string
  time: number
}
interface TreeNode {
  id: string
  name: string
  code?: string
  workcenter?: string
  equipment?: string
  time?: number
  resources?: string[]
  children?: TreeNode[]
}

const processTree = ref<TreeNode[]>([
  {
    id: '1',
    name: '轴类零件-001 工艺规程',
    children: [
      { id: '1-1', name: '下料', code: '10', workcenter: '下料区', equipment: '锯床', time: 0.5, resources: ['锯片'] },
      { id: '1-2', name: '粗车', code: '20', workcenter: '机加一', equipment: '数控车床', time: 2, resources: ['外圆车刀'] },
      { id: '1-3', name: '热处理', code: '30', workcenter: '热处理', equipment: '淬火炉', time: 4, resources: [] },
      { id: '1-4', name: '精车', code: '40', workcenter: '机加一', equipment: '数控车床', time: 1.5, resources: ['精车刀'] },
      { id: '1-5', name: '磨削', code: '50', workcenter: '磨削', equipment: '外圆磨床', time: 1, resources: ['砂轮'] },
      { id: '1-6', name: '检验', code: '60', workcenter: '检验', equipment: '-', time: 0.25, resources: ['卡尺'] },
    ],
  },
  {
    id: '2',
    name: '壳体-002 工艺规程',
    children: [
      { id: '2-1', name: '铸造', code: '10', workcenter: '铸造', equipment: '-', time: 8, resources: [] },
      { id: '2-2', name: '铣面', code: '20', workcenter: '机加二', equipment: '加工中心', time: 3, resources: ['面铣刀'] },
      { id: '2-3', name: '钻孔', code: '30', workcenter: '机加二', equipment: '钻床', time: 1, resources: ['钻头'] },
    ],
  },
])

const routeList = ref<RouteItem[]>([
  { code: '10', name: '下料', workcenter: '下料区', equipment: '锯床', time: 0.5 },
  { code: '20', name: '粗车', workcenter: '机加一', equipment: '数控车床', time: 2 },
  { code: '30', name: '热处理', workcenter: '热处理', equipment: '淬火炉', time: 4 },
  { code: '40', name: '精车', workcenter: '机加一', equipment: '数控车床', time: 1.5 },
  { code: '50', name: '磨削', workcenter: '磨削', equipment: '外圆磨床', time: 1 },
  { code: '60', name: '检验', workcenter: '检验', equipment: '-', time: 0.25 },
])

const cardList = ref([
  { cardNo: 'CAPP-001-10', title: '下料工序卡', type: '机械加工', version: 'v1.0' },
  { cardNo: 'CAPP-001-20', title: '粗车工序卡', type: '机械加工', version: 'v1.0' },
  { cardNo: 'CAPP-001-40', title: '精车工序卡', type: '机械加工', version: 'v1.0' },
])

const selectedNode = ref<TreeNode | null>(null)
function onTreeSelect(_data: TreeNode, node: { level: number; data: TreeNode }) {
  selectedNode.value = node.level > 1 ? node.data : null
}
</script>

<style scoped>
.capp-page {
  height: 100%;
  min-height: 420px;
}
.capp-layout {
  display: flex;
  gap: 16px;
  height: 100%;
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.panel {
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.panel-header {
  padding: 10px 12px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.panel-header.sub {
  font-size: 12px;
  font-weight: 500;
  margin-top: 8px;
}
.left-panel {
  width: 260px;
  flex-shrink: 0;
}
.tree-filter {
  margin: 8px;
  width: calc(100% - 16px);
}
.process-tree {
  flex: 1;
  overflow: auto;
  padding: 0 8px 8px;
}
.tree-node {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.center-panel {
  flex: 1;
  min-width: 0;
}
.center-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
/* 中间栏标签页对齐，与 CAE/CAM 右栏一致 */
.center-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 12px;
}
.center-tabs :deep(.el-tabs__nav-wrap) {
  margin-bottom: 0;
}
.center-tabs :deep(.el-tabs__nav) {
  display: flex;
  width: 100%;
}
.center-tabs :deep(.el-tabs__item) {
  flex: 1;
  text-align: center;
  padding: 0 8px;
}
.center-tabs :deep(.el-tabs__active-bar),
.center-tabs :deep(.el-tabs__ink-bar) {
  height: 2px;
}
.center-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow: auto;
}
.center-tabs :deep(.el-tab-pane) {
  height: 100%;
  padding: 16px 20px;
  box-sizing: border-box;
}
.toolbar {
  margin-bottom: 14px;
  margin-top: 2px;
  display: flex;
  gap: 8px;
  align-items: center;
}
/* 表格与列留出左右间距，避免贴边 */
.center-panel :deep(.el-table td),
.center-panel :deep(.el-table th) {
  padding-left: 14px;
  padding-right: 14px;
}
.center-panel :deep(.el-table th) {
  padding-top: 12px;
  padding-bottom: 12px;
}
.center-panel :deep(.el-table td) {
  padding-top: 10px;
  padding-bottom: 10px;
}
/* 表头不换行 */
.center-panel :deep(.el-table th .cell) {
  white-space: nowrap;
}
.right-panel {
  width: 260px;
  flex-shrink: 0;
}
.props-desc {
  margin: 0 12px;
}
.res-tag {
  margin: 4px 12px 4px 0;
}
</style>
