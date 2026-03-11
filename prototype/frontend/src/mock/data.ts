// Mock 数据

export interface KnowledgeItem {
  id: string
  name: string
  category: string
  type: string
  version: string
  updateTime: string
  status: string
}

export const knowledgeList: KnowledgeItem[] = [
  { id: '1', name: '车削工艺规范', category: '切削工艺', type: '工艺规范', version: 'v2.1', updateTime: '2025-03-01', status: '已发布' },
  { id: '2', name: '铣削参数库', category: '切削工艺', type: '参数库', version: 'v1.5', updateTime: '2025-02-28', status: '已发布' },
  { id: '3', name: '装配工艺卡片模板', category: '装配工艺', type: '模板', version: 'v3.0', updateTime: '2025-03-02', status: '已发布' },
  { id: '4', name: '热处理工艺知识', category: '热处理', type: '工艺规范', version: 'v1.2', updateTime: '2025-02-20', status: '草稿' },
  { id: '5', name: '钣金折弯系数表', category: '钣金工艺', type: '参数库', version: 'v2.0', updateTime: '2025-03-03', status: '已发布' },
]

export interface IntegrationTask {
  id: string
  name: string
  system: 'CAPP' | 'CAM' | 'CAE'
  status: string
  progress: number
  createTime: string
}

export const integrationTasks: IntegrationTask[] = [
  { id: '1', name: '零件工艺规程编制', system: 'CAPP', status: '已完成', progress: 100, createTime: '2025-03-05 09:00' },
  { id: '2', name: 'NC 刀路生成', system: 'CAM', status: '进行中', progress: 65, createTime: '2025-03-05 10:30' },
  { id: '3', name: '结构仿真分析', system: 'CAE', status: '已完成', progress: 100, createTime: '2025-03-04 14:20' },
  { id: '4', name: '工艺卡片导出', system: 'CAPP', status: '排队中', progress: 0, createTime: '2025-03-05 11:00' },
]

export interface ProcessPathItem {
  id: string
  partName: string
  path: string
  steps: number
  status: string
  outputTime: string
}

export const processPathList: ProcessPathItem[] = [
  { id: '1', partName: '轴类零件-001', path: '下料 → 粗车 → 热处理 → 精车 → 磨削 → 检验', steps: 6, status: '已输出', outputTime: '2025-03-05 10:15' },
  { id: '2', partName: '壳体-002', path: '铸造 → 时效 → 铣面 → 钻孔 → 攻丝 → 表面处理', steps: 6, status: '已输出', outputTime: '2025-03-05 09:45' },
  { id: '3', partName: '齿轮-003', path: '下料 → 锻造 → 正火 → 车削 → 滚齿 → 热处理 → 磨齿', steps: 7, status: '生成中', outputTime: '-' },
]

export interface UserItem {
  id: string
  username: string
  role: string
  dept: string
  status: string
  lastLogin: string
}

export const userList: UserItem[] = [
  { id: '1', username: 'admin', role: '系统管理员', dept: '信息化部', status: '正常', lastLogin: '2025-03-05 08:30' },
  { id: '2', username: 'zhangsan', role: '工艺工程师', dept: '工艺部', status: '正常', lastLogin: '2025-03-05 09:15' },
  { id: '3', username: 'lisi', role: '工艺工程师', dept: '工艺部', status: '正常', lastLogin: '2025-03-04 17:20' },
  { id: '4', username: 'wangwu', role: '查看者', dept: '生产部', status: '正常', lastLogin: '2025-03-03 14:00' },
]

export interface RoleItem {
  id: string
  name: string
  code: string
  desc: string
  permissionCount: number
}

export const roleList: RoleItem[] = [
  { id: '1', name: '系统管理员', code: 'admin', desc: '全部功能权限', permissionCount: 99 },
  { id: '2', name: '工艺工程师', code: 'process_engineer', desc: '知识库、工艺设计过程、工艺路径', permissionCount: 45 },
  { id: '3', name: '查看者', code: 'viewer', desc: '仅查看工艺知识库与工艺路径', permissionCount: 12 },
]
