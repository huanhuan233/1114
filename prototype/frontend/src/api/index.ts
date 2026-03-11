/**
 * 后端 API 模块入口
 * 使用示例：
 *   import { knowledgeApi, modelApi } from '@/api'
 *   const { data } = await knowledgeApi.list({ category: '切削工艺' })
 *   knowledgeList.value = data
 */
export { api } from './request'
export type { ApiResult } from './request'
