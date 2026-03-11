<template>
  <div class="page cae-page">
    <div class="cae-layout">
      <!-- 左侧：分析案例 / 仿真项目树 -->
      <div class="panel left-panel">
        <div class="panel-header">
          <span>分析案例</span>
          <el-button size="small" :icon="Upload">导入模型</el-button>
        </div>
        <el-input v-model="treeFilter" placeholder="搜索案例" clearable size="small" class="tree-filter">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-tree
          :data="analysisTree"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          highlight-current
          default-expand-all
          class="analysis-tree"
          @node-click="onNodeSelect"
        >
          <template #default="{ node }">
            <span class="tree-node">
              <el-icon v-if="node.data?.type === 'case'"><FolderOpened /></el-icon>
              <el-icon v-else><DataAnalysis /></el-icon>
              <span>{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
        <div class="panel-header sub">求解类型</div>
        <el-radio-group v-model="solverType" class="solver-list">
          <el-radio-button value="结构静力" />
          <el-radio-button value="模态" />
          <el-radio-button value="热分析" />
        </el-radio-group>
      </div>
      <!-- 中间：仿真结果视图 -->
      <div class="panel center-panel">
        <div class="viewer-toolbar">
          <el-button-group size="small">
            <el-button :icon="Aim">俯视</el-button>
            <el-button :icon="Aim">前视</el-button>
            <el-button :icon="Aim">右视</el-button>
            <el-button :icon="RefreshRight">等轴测</el-button>
          </el-button-group>
          <el-button-group size="small">
            <el-button :icon="ZoomIn">放大</el-button>
            <el-button :icon="ZoomOut">缩小</el-button>
            <el-button :icon="VideoPlay">求解</el-button>
            <el-button :icon="FullScreen">全屏</el-button>
          </el-button-group>
          <el-select v-model="resultType" size="small" style="width: 120px">
            <el-option label="云图" value="contour" />
            <el-option label="变形" value="deform" />
            <el-option label="网格" value="mesh" />
          </el-select>
        </div>
        <div class="viewer-area">
          <div class="viewer-placeholder">
            <el-icon :size="64"><Monitor /></el-icon>
            <p>CAE 仿真结果视图</p>
            <p class="hint">支持结构/热/流体等有限元分析结果云图与后处理</p>
          </div>
        </div>
      </div>
      <!-- 右侧：分析结果 / 边界条件 / 网格 -->
      <div class="panel right-panel">
        <el-tabs v-model="rightTab" class="panel-tabs">
          <el-tab-pane label="分析结果" name="result">
            <template v-if="selectedNode">
              <el-descriptions :column="1" border size="small" class="props-desc">
                <el-descriptions-item label="案例">{{ selectedNode.name }}</el-descriptions-item>
                <el-descriptions-item label="求解类型">{{ solverType }}</el-descriptions-item>
                <el-descriptions-item label="最大应力">{{ selectedNode.maxStress ?? '-' }} MPa</el-descriptions-item>
                <el-descriptions-item label="最大位移">{{ selectedNode.maxDisp ?? '-' }} mm</el-descriptions-item>
                <el-descriptions-item label="安全系数">{{ selectedNode.safetyFactor ?? '-' }}</el-descriptions-item>
                <el-descriptions-item label="求解时间">{{ selectedNode.solveTime ?? '-' }} s</el-descriptions-item>
              </el-descriptions>
              <el-button type="primary" size="small" style="width: 100%; margin-top: 12px">导出报告</el-button>
            </template>
            <el-empty v-else description="在左侧选择分析案例" :image-size="64" />
          </el-tab-pane>
          <el-tab-pane label="边界条件" name="boundary">
            <el-table :data="boundaryList" size="small" max-height="240" stripe>
              <el-table-column prop="name" label="名称" width="90" />
              <el-table-column prop="type" label="类型" width="70" />
              <el-table-column prop="value" label="值" min-width="80" />
            </el-table>
            <el-button size="small" style="width: 100%; margin-top: 8px">添加边界条件</el-button>
          </el-tab-pane>
          <el-tab-pane label="网格" name="mesh">
            <el-descriptions :column="1" border size="small" class="props-desc">
              <el-descriptions-item label="单元数">125,840</el-descriptions-item>
              <el-descriptions-item label="节点数">38,212</el-descriptions-item>
              <el-descriptions-item label="单元类型">四面体</el-descriptions-item>
              <el-descriptions-item label="网格质量">0.82</el-descriptions-item>
            </el-descriptions>
            <el-button size="small" style="width: 100%; margin-top: 12px">重新划分网格</el-button>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Upload, Search, FolderOpened, DataAnalysis, Aim, RefreshRight, ZoomIn, ZoomOut, FullScreen, Monitor, VideoPlay } from '@element-plus/icons-vue'

const treeFilter = ref('')
const solverType = ref('结构静力')
const resultType = ref('contour')
const rightTab = ref('result')

interface AnalysisNode {
  id: string
  name: string
  type: 'case' | 'analysis'
  maxStress?: string
  maxDisp?: string
  safetyFactor?: string
  solveTime?: string
  children?: AnalysisNode[]
}

const analysisTree = ref<AnalysisNode[]>([
  {
    id: '1',
    name: '壳体-002 结构分析',
    type: 'case',
    children: [
      { id: '1-1', name: '静力分析-001', type: 'analysis', maxStress: '185', maxDisp: '0.12', safetyFactor: '2.1', solveTime: '45' },
      { id: '1-2', name: '模态分析-001', type: 'analysis', maxStress: '-', maxDisp: '-', solveTime: '62' },
    ],
  },
  {
    id: '2',
    name: '轴-001 疲劳分析',
    type: 'case',
    children: [
      { id: '2-1', name: '疲劳寿命', type: 'analysis', safetyFactor: '1.8', solveTime: '120' },
    ],
  },
])

const selectedNode = ref<AnalysisNode | null>(null)
function onNodeSelect(_data: AnalysisNode, node: { data: AnalysisNode }) {
  selectedNode.value = node.data
}

const boundaryList = ref([
  { name: '固定约束', type: '位移', value: '底面 全约束' },
  { name: '载荷-1', type: '力', value: '1000 N, Z 向' },
])
</script>

<style scoped>
.cae-page {
  height: 100%;
  min-height: 420px;
}
.cae-layout {
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
  margin-top: 4px;
  border-bottom: none;
}
.left-panel {
  width: 260px;
  flex-shrink: 0;
}
.tree-filter {
  margin: 8px;
  width: calc(100% - 16px);
}
.analysis-tree {
  flex: 1;
  overflow: auto;
  padding: 0 8px 8px;
}
.tree-node {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.solver-list {
  padding: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.solver-list :deep(.el-radio-button__inner) {
  padding: 6px 12px;
}
.center-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.viewer-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-bottom: 1px solid #e4e7ed;
  flex-wrap: wrap;
}
.viewer-area {
  flex: 1;
  min-height: 320px;
  background: #1a2332;
  border-radius: 4px;
  margin: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.viewer-placeholder {
  color: #a0aec0;
  text-align: center;
}
.viewer-placeholder .hint {
  font-size: 12px;
  margin-top: 8px;
  color: #718096;
}
.right-panel {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.panel-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.panel-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 12px;
}
.panel-tabs :deep(.el-tabs__nav-wrap) {
  margin-bottom: 0;
}
.panel-tabs :deep(.el-tabs__nav) {
  display: flex;
  width: 100%;
}
.panel-tabs :deep(.el-tabs__item) {
  flex: 1;
  text-align: center;
  padding: 0 8px;
}
.panel-tabs :deep(.el-tabs__active-bar),
.panel-tabs :deep(.el-tabs__ink-bar) {
  height: 2px;
}
.panel-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow: auto;
  padding: 0 12px 12px;
}
.panel-tabs :deep(.el-tab-pane) {
  height: 100%;
}
.props-desc {
  margin: 0;
}
</style>
