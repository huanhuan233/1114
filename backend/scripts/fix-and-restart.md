# 修复 500 错误并重启后端

## 原因说明

之前建表时 `integration_task` 使用了 MySQL 保留字 `system` 导致 DDL 失败，部分表可能未创建，访问 `/api/knowledge`、`/api/models` 时会报 500。

已做两处修改：

1. **实体字段**：`IntegrationTask.system` 对应列名改为 `task_system`，避免保留字。
2. **全局异常**：增加 `GlobalExceptionHandler`，500 时会返回 JSON 错误信息，便于排查。

## 操作步骤

1. **停止当前后端**  
   在运行 `mvn spring-boot:run` 的终端按 `Ctrl+C`。

2. **重新启动后端**  
   ```powershell
   cd "D:\科研i项目\1114\原型\backend"
   mvn spring-boot:run
   ```

3. 等待出现 `Started ProcessAgentApplication`，再刷新前端页面。

重启后 Hibernate（ddl-auto: update）会补建缺失表（如 `knowledge_item`、`model_config` 等），并用新列名 `task_system` 创建 `integration_task`，初始化数据会再次执行。

若仍有 500，请查看后端控制台完整报错，或看接口返回的 JSON 里的 `message` 字段（已由全局异常处理返回）。
