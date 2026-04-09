# 工艺智能体平台 - 原型

Vue3 + TypeScript + Element Plus 工艺智能体平台原型，包含工艺知识库、CAPP/CAM/CAE 集成、工艺路径输出、权限管理，使用 Mock 数据。

## 运行

```bash
npm install
npm run dev
```

浏览器访问终端中显示的本地地址（如 `http://localhost:5173`）即可查看原型。

## 功能模块

| 模块           | 说明 |
|----------------|------|
| 工艺知识库     | 知识条目列表、分类/类型筛选、增删改查（Mock） |
| CAPP/CAM/CAE 集成 | 三系统卡片入口、集成任务列表、新建任务、进度展示 |
| 工艺路径输出   | 工艺路径列表、按零件/状态筛选、路径工序标签、导出/复制 |
| 权限管理       | 用户管理、角色管理、菜单权限树配置 |

## 对接后端

项目已配置 Vite 代理：开发时 `/api` 会转发到 `http://localhost:8080`。请先启动后端（见仓库内 `backend/README.md`），再运行前端。请求封装见 `src/api/request.ts`，可将各页面由 Mock 改为调用 `api.get('/knowledge')`、`api.get('/models')` 等接口。

## 技术栈

- Vue 3 (Composition API + `<script setup>`)
- TypeScript
- Vue Router 4
- Element Plus
- Vite 5
