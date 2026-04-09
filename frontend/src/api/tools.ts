import { api } from './request'

export interface ToolItem {
  id: string
  name: string
  type: 'mcp' | 'api' | 'custom' | string
  endpoint: string
  status: '已启用' | '已停用' | string
  desc: string
  updateTime?: string
}

export function listTools(params?: { type?: string; status?: string; keyword?: string }) {
  const search = new URLSearchParams()
  if (params?.type) search.set('type', params.type)
  if (params?.status) search.set('status', params.status)
  if (params?.keyword) search.set('keyword', params.keyword)
  const q = search.toString()
  return api.get<ToolItem[]>(`/tools${q ? `?${q}` : ''}`)
}

export function getTool(id: string) {
  return api.get<ToolItem>(`/tools/${id}`)
}

export function createTool(body: Partial<ToolItem>) {
  return api.post<ToolItem>('/tools', body)
}

export function updateTool(id: string, body: Partial<ToolItem>) {
  return api.put<ToolItem>(`/tools/${id}`, body)
}

export function deleteTool(id: string) {
  return api.delete<void>(`/tools/${id}`)
}

