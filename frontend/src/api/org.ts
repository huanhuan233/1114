import { api } from './request'

export interface OrgNode {
  id: string
  name: string
  shortName?: string
  code: string
  orgType?: string
  parentId?: string | number | null
  headCount?: number
  sortOrder?: number
  children?: OrgNode[]
}

export function getOrgTree() {
  return api.get<OrgNode[]>('/org/tree')
}

export function listOrg() {
  return api.get<OrgNode[]>('/org')
}

export function getOrg(id: string) {
  return api.get<OrgNode>(`/org/${id}`)
}

export function createOrg(body: {
  name: string
  shortName?: string
  code?: string
  orgType?: string
  parentId?: number | null
  headCount?: number
  sortOrder?: number
}) {
  return api.post<OrgNode>('/org', body)
}

export function updateOrg(
  id: string,
  body: Partial<{ name: string; shortName: string; code: string; orgType: string; parentId: number | null; headCount: number; sortOrder: number }>
) {
  return api.put<OrgNode>(`/org/${id}`, body)
}

export function deleteOrg(id: string) {
  return api.delete<void>(`/org/${id}`)
}
