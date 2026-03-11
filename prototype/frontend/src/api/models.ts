import { api } from './request'

export interface ModelItem {
  id: string
  name: string
  type: string
  provider: string
  endpoint: string
  status: string
  updateTime: string
}

export function listModels(params?: { type?: string; keyword?: string }) {
  const search = new URLSearchParams()
  if (params?.type) search.set('type', params.type)
  if (params?.keyword) search.set('keyword', params.keyword)
  const q = search.toString()
  return api.get<ModelItem[]>(`/models${q ? `?${q}` : ''}`)
}

export function getModel(id: string) {
  return api.get<ModelItem>(`/models/${id}`)
}

export function createModel(body: Partial<ModelItem> & { apiKey?: string; remark?: string }) {
  return api.post<ModelItem>('/models', body)
}

export function updateModel(id: string, body: Partial<ModelItem> & { apiKey?: string; remark?: string }) {
  return api.put<ModelItem>(`/models/${id}`, body)
}

export function deleteModel(id: string) {
  return api.delete<void>(`/models/${id}`)
}
