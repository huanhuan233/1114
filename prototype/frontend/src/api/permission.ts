import { api } from './request'

export interface UserItem {
  id: string
  employeeNo?: string
  realName?: string
  username: string
  personnelCategory?: string
  positionType?: string
  jobTitle?: string
  dept: string
  role: string
  roleId?: number | null
  contact?: string
  status: string
  lastLogin: string
  loginTime: string
  logoutTime: string
}

export interface RoleItem {
  id: string
  name: string
  code: string
  desc: string
  permissionCount: number
}

export function listUsers() {
  return api.get<UserItem[]>('/permission/users')
}

export function listRoles() {
  return api.get<RoleItem[]>('/permission/roles')
}

export function createUser(body: {
  username: string
  employeeNo?: string
  realName?: string
  personnelCategory?: string
  positionType?: string
  jobTitle?: string
  roleId?: number
  dept?: string
  contact?: string
  status?: string
}) {
  return api.post<UserItem>('/permission/users', body)
}

export function updateUser(
  id: string,
  body: Partial<{
    username: string
    employeeNo: string
    realName: string
    personnelCategory: string
    positionType: string
    jobTitle: string
    roleId: number
    dept: string
    contact: string
    status: string
  }>
) {
  return api.put<UserItem>(`/permission/users/${id}`, body)
}

export function deleteUser(id: string) {
  return api.delete<void>(`/permission/users/${id}`)
}

export function createRole(body: { name: string; code: string; desc?: string; permissionCount?: number }) {
  return api.post<RoleItem>('/permission/roles', body)
}

export function updateRole(id: string, body: Partial<{ name: string; code: string; desc: string; permissionCount: number }>) {
  return api.put<RoleItem>(`/permission/roles/${id}`, body)
}

export function deleteRole(id: string) {
  return api.delete<void>(`/permission/roles/${id}`)
}

/** 权限树节点（菜单/功能） */
export interface MenuTreeNode {
  id: string
  name: string
  children?: MenuTreeNode[]
}

export function getMenuTree() {
  return api.get<MenuTreeNode[]>('/permission/menu-tree')
}

export function getRolePermissions(roleId: string) {
  return api.get<string[]>(`/permission/roles/${roleId}/permissions`)
}

export function saveRolePermissions(roleId: string, permissionIds: string[]) {
  return api.put<void>(`/permission/roles/${roleId}/permissions`, { permissionIds })
}

/** 日志统计：总计运行时间(秒)、系统实际运行时间(秒)，可按时间范围查询 */
export interface LogStats {
  totalRunTimeSeconds: number
  systemActualRunTimeSeconds: number
  startTime?: string
  endTime?: string
}

export function getLogStats(params?: { startTime?: string; endTime?: string }) {
  const search = new URLSearchParams()
  if (params?.startTime) search.set('startTime', params.startTime)
  if (params?.endTime) search.set('endTime', params.endTime)
  const q = search.toString()
  return api.get<LogStats>(`/permission/log-stats${q ? `?${q}` : ''}`)
}
