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
  /** 关联自动生成的工作流应用 ID，存在时可进入工作流页 */
  appId?: number
  /** 关联工作流应用名称 */
  appName?: string
  /** 推荐工作流模板标识 */
  templateKey?: string
  /** 创建任务时的初始工作流 JSON 快照 */
  workflowJsonSnapshot?: string
}

export function listIntegrationTasks(params?: { system?: string; status?: string; appPath?: string }) {
  const search = new URLSearchParams()
  if (params?.system) search.set('system', params.system)
  if (params?.status) search.set('status', params.status)
  if (params?.appPath) search.set('appPath', params.appPath)
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
