import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/home',
      children: [
        { path: 'home', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '航空轻量化构件制造工艺智能设计软件系统' } },
        { path: 'knowledge', name: 'Knowledge', component: () => import('@/views/KnowledgeBase.vue'), meta: { title: '复杂结构件制造工艺知识库软件' } },
        { path: 'knowledge/portal', name: 'KnowledgePortal', component: () => import('@/views/KnowledgePortal.vue'), meta: { title: '知识库门户' } },
        {
          path: 'integration',
          component: () => import('@/layouts/IntegrationLayout.vue'),
          redirect: '/integration',
          children: [
            { path: '', name: 'Integration', component: () => import('@/views/Integration.vue'), meta: { title: '工艺设计过程' } },
            {
              path: 'process-review',
              component: () => import('@/layouts/ProcessReviewLayout.vue'),
              redirect: '/integration/process-review/feasibility',
              meta: { title: '可制造性分析与优化软件' },
              children: [
                { path: 'structure', name: 'StructureAnalysisApp', component: () => import('@/views/integration/StructureAnalysisApp.vue'), meta: { title: '结构合理性分析APP' } },
                { path: 'feasibility', name: 'FeasibilityOptimizationApp', component: () => import('@/views/integration/FeasibilityOptimizationApp.vue'), meta: { title: '工艺方案可行性评估和优化APP' } },
              ],
            },
            {
              path: 'capp',
              component: () => import('@/layouts/CAPPLayout.vue'),
              redirect: '/integration/capp/special',
              children: [
                { path: 'special', name: 'SpecialProcessApp', component: () => import('@/views/integration/SpecialProcessApp.vue'), meta: { title: '特种工艺决策与指令生成APP' } },
                { path: 'laying', name: 'LayingProcessApp', component: () => import('@/views/integration/LayingProcessApp.vue'), meta: { title: '复材铺放工艺决策APP' } },
                { path: 'filament', name: 'FilamentProcessApp', component: () => import('@/views/integration/FilamentProcessApp.vue'), meta: { title: '复材铺丝工艺编程APP' } },
                { path: 'conduit', name: 'ConduitProcessApp', component: () => import('@/views/integration/ConduitProcessApp.vue'), meta: { title: '导管成形工艺仿真APP' } },
                { path: 'superplastic', name: 'SuperplasticProcessApp', component: () => import('@/views/integration/SuperplasticProcessApp.vue'), meta: { title: '超塑成形工艺仿真APP' } },
              ],
            },
            {
              path: 'cae',
              component: () => import('@/layouts/CAELayout.vue'),
              redirect: '/integration/cae/process',
              children: [
                { path: 'process', name: 'CAEProcessApp', component: () => import('@/views/integration/CAEProcessApp.vue'), meta: { title: '复材制造工艺过程仿真APP' } },
                { path: 'solver', name: 'CAESolverApp', component: () => import('@/views/integration/CAESolverApp.vue'), meta: { title: '渗流求解器与多物理耦合器APP' } },
              ],
            },
          ],
        },
        {
          path: 'agent-config',
          component: () => import('@/layouts/AgentConfigLayout.vue'),
          redirect: '/agent-config/studio',
          children: [
            { path: 'studio', name: 'Workbench', component: () => import('@/views/agent/Workbench.vue'), meta: { title: '工作台' } },
            { path: 'models', name: 'ModelConfig', component: () => import('@/views/agent/ModelConfig.vue'), meta: { title: '模型管理' } },
            { path: 'rule', name: 'RuleConfig', component: () => import('@/views/agent/RuleConfig.vue'), meta: { title: '规则配置' } },
            { path: 'workflow', name: 'Workflow', component: () => import('@/views/agent/Workflow.vue'), meta: { title: '智能体配置' } },
            { path: 'tools', name: 'ToolManage', component: () => import('@/views/agent/ToolManage.vue'), meta: { title: '工具管理' } },
          ],
        },
        { path: 'system', name: 'SystemManagement', component: () => import('@/views/SystemManagement.vue'), meta: { title: '系统管理' } },
      ],
    },
  ],
})

export default router
