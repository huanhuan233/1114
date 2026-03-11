/**
 * 后端 API 请求封装，与 process-agent-backend 对接
 * 开发时通过 vite 代理将 /api 转发到 http://localhost:8080
 */

export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

const baseURL = '/api'

async function request<T>(url: string, options?: RequestInit): Promise<ApiResult<T>> {
  const res = await fetch(`${baseURL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  })
  let json: ApiResult<T>
  try {
    json = (await res.json()) as ApiResult<T>
  } catch {
    throw new Error(res.status === 0 ? '网络错误，请确认后端已启动' : '响应解析失败')
  }
  if (json.code !== 200) {
    throw new Error(json.message || '请求失败')
  }
  return json
}

export const api = {
  get<T>(url: string) {
    return request<T>(url, { method: 'GET' })
  },
  post<T>(url: string, body: unknown) {
    return request<T>(url, { method: 'POST', body: JSON.stringify(body) })
  },
  put<T>(url: string, body: unknown) {
    return request<T>(url, { method: 'PUT', body: JSON.stringify(body) })
  },
  delete<T>(url: string) {
    return request<T>(url, { method: 'DELETE' })
  },
}
