import { api } from './request'

export interface RuleItem {
  id: string
  name: string
  naturalLanguage: string
  logicOutput: string
  status: string
  updateTime: string
}

export function listRules(params?: { status?: string; keyword?: string }) {
  const search = new URLSearchParams()
  if (params?.status) search.set('status', params.status)
  if (params?.keyword) search.set('keyword', params.keyword)
  const q = search.toString()
  return api.get<RuleItem[]>(`/rules${q ? `?${q}` : ''}`)
}

export function getRule(id: string) {
  return api.get<RuleItem>(`/rules/${id}`)
}

export function createRule(body: Partial<RuleItem>) {
  return api.post<RuleItem>('/rules', body)
}

export function updateRule(id: string, body: Partial<RuleItem>) {
  return api.put<RuleItem>(`/rules/${id}`, body)
}

export function deleteRule(id: string) {
  return api.delete<void>(`/rules/${id}`)
}

