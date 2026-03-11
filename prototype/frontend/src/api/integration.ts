import { api } from './request'

export interface IntegrationTask {
  id: string
  name: string
  system: string
  status: string
  progress: number
  createTime: string
  startTime: string
  endTime: string
  runTimeSeconds: number
}

export function listIntegrationTasks(params?: { system?: string; status?: string }) {
  const search = new URLSearchParams()
  if (params?.system) search.set('system', params.system)
  if (params?.status) search.set('status', params.status)
  const q = search.toString()
  return api.get<IntegrationTask[]>(`/integration/tasks${q ? `?${q}` : ''}`)
}

export function createIntegrationTask(body: {
  name: string
  system: string
  status?: string
  progress?: number
  startTime?: string
  endTime?: string
  runTimeSeconds?: number
}) {
  return api.post<IntegrationTask>('/integration/tasks', body)
}

export function updateIntegrationTask(
  id: string,
  body: Partial<{ name: string; system: string; status: string; progress: number; startTime: string; endTime: string; runTimeSeconds: number }>
) {
  return api.put<IntegrationTask>(`/integration/tasks/${id}`, body)
}

export function deleteIntegrationTask(id: string) {
  return api.delete<void>(`/integration/tasks/${id}`)
}
