import { api } from './request'

export interface ProcessPathItem {
  id: string
  partName: string
  path: string
  steps: number
  status: string
  outputTime: string
}

export function listProcessPath(params?: { partName?: string; status?: string }) {
  const search = new URLSearchParams()
  if (params?.partName) search.set('partName', params.partName)
  if (params?.status) search.set('status', params.status)
  const q = search.toString()
  return api.get<ProcessPathItem[]>(`/process-path${q ? `?${q}` : ''}`)
}

export function getProcessPath(id: string) {
  return api.get<ProcessPathItem>(`/process-path/${id}`)
}

export function createProcessPath(body: Partial<ProcessPathItem>) {
  return api.post<ProcessPathItem>('/process-path', body)
}

export function updateProcessPath(id: string, body: Partial<ProcessPathItem>) {
  return api.put<ProcessPathItem>(`/process-path/${id}`, body)
}

export function deleteProcessPath(id: string) {
  return api.delete<void>(`/process-path/${id}`)
}
