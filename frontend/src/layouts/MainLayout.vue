<template>
  <el-container class="main-layout">
    <el-aside :width="asideWidth" class="aside">
      <div class="logo">
        <el-icon :size="28"><Setting /></el-icon>
        <span v-show="!collapsed" class="title-ellipsis" title="航空轻量化构件制造工艺智能设计软件系统">制造工艺智能设计软件系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :default-openeds="defaultOpeneds"
        :collapse="collapsed"
        router
        background-color="#1a2332"
        text-color="#a0aec0"
        active-text-color="#409eff"
      >
        <el-menu-item index="/home">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-sub-menu index="knowledge">
          <template #title>
            <el-icon><Collection /></el-icon>
            <span class="title-ellipsis" title="复杂结构件制造工艺知识库软件">复杂结构件制造工艺知识库软件</span>
          </template>
          <el-menu-item index="/knowledge/portal">
            <el-icon><Link /></el-icon>
            <template #title>知识库门户</template>
          </el-menu-item>
          <el-menu-item index="/knowledge">
            <el-icon><Document /></el-icon>
            <template #title>本地知识库</template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="integration">
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>工艺设计过程</span>
          </template>
          <el-menu-item index="/integration">
            <el-icon><List /></el-icon>
            <template #title>任务管理</template>
          </el-menu-item>
          <el-sub-menu index="process-review">
            <template #title>
              <el-icon><DocumentChecked /></el-icon>
              <span class="title-ellipsis" title="可制造性分析与优化软件">可制造性分析与优化软件</span>
            </template>
            <el-menu-item index="/integration/process-review/structure">
              <el-icon><Grid /></el-icon>
              <template #title>结构合理性分析APP</template>
            </el-menu-item>
            <el-menu-item index="/integration/process-review/feasibility">
              <el-icon><TrendCharts /></el-icon>
              <template #title>工艺方案可行性评估和优化APP</template>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="capp">
            <template #title>
              <el-icon><Document /></el-icon>
              <span class="title-ellipsis" title="特种工艺决策与指令生成软件">特种工艺决策与指令生成软件</span>
            </template>
            <el-menu-item index="/integration/capp/special">
              <el-icon><Tickets /></el-icon>
              <template #title>特种工艺决策与指令生成APP</template>
            </el-menu-item>
            <el-menu-item index="/integration/capp/laying">
              <el-icon><Operation /></el-icon>
              <template #title>复材铺放工艺决策APP</template>
            </el-menu-item>
            <el-menu-item index="/integration/capp/filament">
              <el-icon><Connection /></el-icon>
              <template #title>复材铺丝工艺编程APP</template>
            </el-menu-item>
            <el-menu-item index="/integration/capp/conduit">
              <el-icon><DataLine /></el-icon>
              <template #title>导管成形工艺仿真APP</template>
            </el-menu-item>
            <el-menu-item index="/integration/capp/superplastic">
              <el-icon><DataAnalysis /></el-icon>
              <template #title>超塑成形工艺仿真APP</template>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="cae">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span class="title-ellipsis" title="复材成型工艺高精度仿真软件">复材成型工艺高精度仿真软件</span>
            </template>
            <el-menu-item index="/integration/cae/process">
              <el-icon><VideoPlay /></el-icon>
              <template #title>复材制造工艺过程仿真APP</template>
            </el-menu-item>
            <el-menu-item index="/integration/cae/solver">
              <el-icon><Connection /></el-icon>
              <template #title>渗流求解器与多物理耦合器APP</template>
            </el-menu-item>
          </el-sub-menu>
        </el-sub-menu>
        <el-sub-menu index="agent-config">
          <template #title>
            <el-icon><MagicStick /></el-icon>
            <span>智能体管理</span>
          </template>
          <el-menu-item index="/agent-config/studio">
            <el-icon><Monitor /></el-icon>
            <template #title>工作台</template>
          </el-menu-item>
          <el-menu-item index="/agent-config/models">
            <el-icon><Coin /></el-icon>
            <template #title>模型管理</template>
          </el-menu-item>
          <el-menu-item index="/agent-config/rule">
            <el-icon><Document /></el-icon>
            <template #title>规则配置</template>
          </el-menu-item>
          <el-menu-item index="/agent-config/workflow">
            <el-icon><Share /></el-icon>
            <template #title>智能体配置</template>
          </el-menu-item>
          <el-menu-item index="/agent-config/tools">
            <el-icon><Box /></el-icon>
            <template #title>工具管理</template>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/system">
          <el-icon><Setting /></el-icon>
          <template #title>系统管理</template>
        </el-menu-item>
      </el-menu>
      <div class="collapse-btn" @click="collapsed = !collapsed">
        <el-icon :size="20">
          <Fold v-if="!collapsed" />
          <Expand v-else />
        </el-icon>
        <span v-show="!collapsed">收起</span>
      </div>
    </el-aside>
    <el-container class="main-body">
      <el-header class="header">
        <span class="page-title title-ellipsis" :title="pageTitle">{{ pageTitle }}</span>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              admin
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <div class="main-inner">
          <router-view v-slot="{ Component, route: currentRoute }">
            <transition name="fade" mode="out-in">
              <div v-if="childError" key="error" class="main-empty">
                <el-icon :size="48"><WarningFilled /></el-icon>
                <p>页面加载失败或不存在</p>
                <p class="main-empty-hint">刷新后一般可恢复</p>
                <div class="main-empty-actions">
                  <el-button type="primary" @click="goHome">返回首页</el-button>
                  <el-button @click="reloadPage">刷新页面</el-button>
                </div>
              </div>
              <div v-else-if="Component" :key="layoutLevelKey(currentRoute)" class="main-content-wrap">
                <Suspense>
                  <component :is="Component" />
                  <template #fallback>
                    <div class="main-loading">
                      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
                      <p>加载中…</p>
                    </div>
                  </template>
                </Suspense>
              </div>
              <div v-else key="empty" class="main-empty">
                <el-icon :size="48"><WarningFilled /></el-icon>
                <p>页面加载失败或不存在</p>
                <p class="main-empty-hint">刷新后一般可恢复</p>
                <div class="main-empty-actions">
                  <el-button type="primary" @click="goHome">返回首页</el-button>
                  <el-button @click="reloadPage">刷新页面</el-button>
                </div>
              </div>
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, watch, onErrorCaptured } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { Fold, Expand, Loading, WarningFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)
const childError = ref(false)

watch(() => route.path, () => { childError.value = false }, { immediate: true })

/** 用“布局级”路径做 key，避免在仅切换子路由时销毁整块布局导致空白 */
function layoutLevelKey(r: RouteLocationNormalizedLoaded): string {
  const top = r.matched[1]
  return top ? (top.path || r.path) : r.path
}

onErrorCaptured((err: unknown) => {
  childError.value = true
  console.error('[MainLayout] 子组件渲染错误', err)
  return false
})

function goHome() {
  router.push('/home')
}
function reloadPage() {
  window.location.reload()
}
const activeMenu = computed(() => route.path)
const defaultOpeneds = computed(() => {
  const opens: string[] = []
  if (route.path.startsWith('/knowledge')) opens.push('knowledge')
  if (route.path.startsWith('/integration')) opens.push('integration')
  if (route.path.startsWith('/integration/process-review')) opens.push('process-review')
  if (route.path.startsWith('/integration/capp')) opens.push('capp')
  if (route.path.startsWith('/integration/cae')) opens.push('cae')
  if (route.path.startsWith('/agent-config')) opens.push('agent-config')
  return opens
})
const pageTitle = computed(() => (route.meta?.title as string) || '航空轻量化构件制造工艺智能设计软件系统')
const asideWidth = computed(() => (collapsed.value ? '64px' : '220px'))
</script>

<style scoped>
.main-layout {
  height: 100vh;
  display: flex;
  flex-direction: row;
}
.main-layout > .el-container.main-body {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.main-body > .el-main.main {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 20px;
}
.main-inner {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  position: relative;
}
.aside {
  background: #1a2332;
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
}
.logo {
  height: 56px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #e2e8f0;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 1px solid #2d3748;
  overflow: hidden;
  min-width: 0;
}
.logo .title-ellipsis {
  flex: 1;
  min-width: 0;
}
.el-menu {
  border-right: none;
  flex: 1;
  overflow-x: hidden;
  overflow-y: auto;
  min-height: 0;
}
.collapse-btn {
  flex-shrink: 0;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #a0aec0;
  cursor: pointer;
  border-top: 1px solid #2d3748;
  transition: background 0.2s;
}
.collapse-btn:hover {
  background: #2d3748;
  color: #e2e8f0;
}
.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  max-width: 60vw;
  display: inline-block;
}
.title-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.aside :deep(.el-sub-menu__title) {
  min-width: 0;
}
.aside :deep(.el-sub-menu__title .title-ellipsis),
.aside :deep(.el-menu-item .title-ellipsis) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 100%;
  vertical-align: middle;
}
.aside :deep(.el-menu-item) {
  min-width: 0;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #606266;
}
.main {
  background: #f0f2f5;
  overflow: auto;
}
.main-inner > :deep(*) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.main-content-wrap {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.main-content-wrap > :deep(*) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
/* 左侧菜单栏滚动条深色 */
.aside :deep(.el-menu)::-webkit-scrollbar,
.aside::-webkit-scrollbar {
  width: 6px;
}
.aside :deep(.el-menu)::-webkit-scrollbar-track,
.aside::-webkit-scrollbar-track {
  background: #252b3b;
  border-radius: 3px;
}
.aside :deep(.el-menu)::-webkit-scrollbar-thumb,
.aside::-webkit-scrollbar-thumb {
  background: #4a5568;
  border-radius: 3px;
}
.aside :deep(.el-menu)::-webkit-scrollbar-thumb:hover,
.aside::-webkit-scrollbar-thumb:hover {
  background: #718096;
}
.main-loading,
.main-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 280px;
  color: #909399;
  gap: 12px;
}
.main-empty p {
  margin: 0;
  font-size: 14px;
}
.main-empty-hint {
  font-size: 12px !important;
  color: #c0c4cc;
}
.main-empty-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.main-loading p {
  margin: 0;
  font-size: 14px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
