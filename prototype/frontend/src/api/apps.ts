import { api } from './request'

export interface AppItem {
  id: string
  name: string
  desc: string
  type: string
  status: string
  publicUrl: string
  createTime: string
  updateTime: string
  publishTime: string
  workflowJson?: string
}

export function listApps(params?: { status?: string; type?: string; keyword?: string }) {
  const search = new URLSearchParams()
  if (params?.status) search.set('status', params.status)
  if (params?.type) search.set('type', params.type)
  if (params?.keyword) search.set('keyword', params.keyword)
  const q = search.toString()
  return api.get<AppItem[]>(`/apps${q ? `?${q}` : ''}`)
}

export function getApp(id: string) {
  return api.get<AppItem>(`/apps/${id}`)
}

export function createApp(body: Partial<AppItem> & { name: string; desc?: string; type?: string }) {
  return api.post<AppItem>('/apps', body)
}

export function updateApp(id: string, body: Partial<AppItem>) {
  return api.put<AppItem>(`/apps/${id}`, body)
}

export function saveWorkflow(id: string, body: { workflowJson?: string; nodes?: unknown[] }) {
  return api.put<AppItem>(`/apps/${id}/workflow`, body)
}

export function publishApp(id: string) {
  return api.post<AppItem>(`/apps/${id}/publish`, {})
}

export function deleteApp(id: string) {
  return api.delete<void>(`/apps/${id}`)
}

