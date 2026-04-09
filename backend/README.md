# 工艺智能体平台 - 后端

基于 **JDK 17**、**Maven**、**Spring Boot 3.2**、**Spring Cloud** 与 **Spring Cloud Alibaba** 技术栈，数据库为 **MySQL 5.7+**。

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 5.7 或 8.x

## 数据库准备

在 MySQL 中创建库（若不存在）：

```sql
CREATE DATABASE IF NOT EXISTS process_agent
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

首次启动时 JPA 会按实体自动建表（`ddl-auto: update`）。

## 配置

- 主配置：`src/main/resources/application.yml`
- 数据库账号密码可在 `application.yml` 中修改，或新建 `application-dev.yml`（可参考 `application-dev.yml.example`）覆盖：

```yaml
spring:
  datasource:
    username: root
    password: 你的密码
```

## 运行

```bash
cd backend
mvn spring-boot:run
```

服务默认端口：**8080**。首次启动会自动插入工艺知识库、模型配置、工艺路径、集成任务、用户与角色等基础数据。

## API 说明（与前端对接）

所有接口前缀：`/api`，统一返回格式：`{ "code": 200, "message": "success", "data": ... }`。

| 模块       | 方法 | 路径 | 说明 |
|------------|------|------|------|
| 工艺知识库 | GET  | /api/knowledge | 列表，可选 query: category, type, keyword |
| 工艺知识库 | GET  | /api/knowledge/{id} | 详情 |
| 工艺知识库 | POST | /api/knowledge | 新增 |
| 工艺知识库 | PUT  | /api/knowledge/{id} | 更新 |
| 工艺知识库 | DELETE | /api/knowledge/{id} | 删除 |
| 模型配置   | GET  | /api/models | 列表，可选 query: type, keyword |
| 模型配置   | GET  | /api/models/{id} | 详情 |
| 模型配置   | POST | /api/models | 新增 |
| 模型配置   | PUT  | /api/models/{id} | 更新 |
| 模型配置   | DELETE | /api/models/{id} | 删除 |
| 工艺路径   | GET  | /api/process-path | 列表，可选 query: partName, status |
| 工艺路径   | GET  | /api/process-path/{id} | 详情 |
| 工艺路径   | POST | /api/process-path | 新增 |
| 工艺路径   | PUT  | /api/process-path/{id} | 更新 |
| 工艺路径   | DELETE | /api/process-path/{id} | 删除 |
| 集成任务   | GET  | /api/integration/tasks | 列表 |
| 集成任务   | GET  | /api/integration/tasks/{id} | 详情 |
| 集成任务   | POST | /api/integration/tasks | 新增 |
| 集成任务   | PUT  | /api/integration/tasks/{id} | 更新 |
| 集成任务   | DELETE | /api/integration/tasks/{id} | 删除 |
| 权限-用户  | GET  | /api/permission/users | 用户列表 |
| 权限-用户  | GET  | /api/permission/users/{id} | 用户详情 |
| 权限-用户  | POST | /api/permission/users | 新增用户 |
| 权限-用户  | PUT  | /api/permission/users/{id} | 更新用户 |
| 权限-用户  | DELETE | /api/permission/users/{id} | 删除用户 |
| 权限-角色  | GET  | /api/permission/roles | 角色列表 |
| 权限-角色  | GET  | /api/permission/roles/{id} | 角色详情 |
| 权限-角色  | POST | /api/permission/roles | 新增角色 |
| 权限-角色  | PUT  | /api/permission/roles/{id} | 更新角色 |
| 权限-角色  | DELETE | /api/permission/roles/{id} | 删除角色 |

前端开发时请将请求 baseURL 指向 `http://localhost:8080`，并确保后端已开启 CORS（已配置 `/api/**`）。

## 后续扩展（Spring Cloud Alibaba）

- 接入 **Nacos**：在 `application.yml` 中配置 `spring.cloud.nacos.discovery.server-addr` 与 `spring.cloud.nacos.config.server-addr`，并增加依赖 `spring-cloud-starter-alibaba-nacos-discovery`、`spring-cloud-starter-alibaba-nacos-config`。
- 拆分为多服务后可增加 **Spring Cloud Gateway** 做统一网关与路由。
