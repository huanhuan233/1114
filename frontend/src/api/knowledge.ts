import { api } from './request'

export interface KnowledgeItem {
  id: string
  name: string
  category: string
  type: string
  version: string
  updateTime: string
  status: string
}

export function listKnowledge(params?: { category?: string; type?: string; keyword?: string }) {
  const search = new URLSearchParams()
  if (params?.category) search.set('category', params.category)
  if (params?.type) search.set('type', params.type)
  if (params?.keyword) search.set('keyword', params.keyword)
  const q = search.toString()
  return api.get<KnowledgeItem[]>(`/knowledge${q ? `?${q}` : ''}`)
}

export function getKnowledge(id: string) {
  return api.get<KnowledgeItem>(`/knowledge/${id}`)
}

export function createKnowledge(body: Partial<KnowledgeItem>) {
  return api.post<KnowledgeItem>('/knowledge', body)
}

export function updateKnowledge(id: string, body: Partial<KnowledgeItem>) {
  return api.put<KnowledgeItem>(`/knowledge/${id}`, body)
}

export function deleteKnowledge(id: string) {
  return api.delete<void>(`/knowledge/${id}`)
}
