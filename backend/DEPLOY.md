# 本地环境部署说明（Windows）

仅部署与建库，不涉及新开发。按下列步骤操作即可运行后端。

## 1. 环境要求

- **JDK 17+**（建议用 Oracle JDK 或 OpenJDK 17）
- **Maven 3.8+**
- **MySQL 5.7 或 8.x**（你本机已安装的 SQL 为本项目使用的 MySQL）

## 2. 启动 MySQL 服务

确保 MySQL 服务已启动（如 Windows 服务中的 “MySQL80” 或 “MySQL57”）。

## 3. 创建数据库

任选一种方式执行建库脚本。

### 方式 A：用项目提供的批处理（推荐）

在项目根目录下打开命令行，执行：

```bat
cd backend\scripts
create-db.bat
```

按提示输入本机 MySQL **root** 密码。执行成功后会出现 `process_agent` 库。

### 方式 B：用 MySQL 命令行

若 `mysql` 已在 PATH 中：

```bat
cd F:\1114\prototype\prototype\backend\scripts
mysql -u root -p < create-db.sql
```

输入 root 密码即可。

### 方式 C：用 MySQL Workbench 等图形工具

打开 `backend\scripts\create-db.sql`，在 Workbench 中连接本机 MySQL，执行该 SQL 文件。

建库脚本内容为：

```sql
CREATE DATABASE IF NOT EXISTS process_agent
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

首次启动后端时，JPA 会根据实体**自动建表**（`ddl-auto: update`），无需再手动画表。

## 4. 配置数据库连接

- **主配置**：`backend\src\main\resources\application.yml`  
  其中已有 `spring.datasource.username`、`password`，若你本机 MySQL 的 root 密码就是这里的值，可直接启动，无需改配置。

- **使用本地密码（推荐）**：不改动 `application.yml`，用本地配置覆盖：
  1. 打开 `backend\src\main\resources\application-dev.yml`
  2. 将 `password: 你的密码` 改成你本机 MySQL 的 **root 密码**
  3. 启动时激活 dev 配置（见下）

## 5. 启动后端

在项目根目录下：

```bat
cd backend
mvn spring-boot:run
```

若使用 `application-dev.yml` 中的密码，请改为：

```bat
cd backend
mvn spring-boot:run -Dspring.profiles.active=dev
```

服务默认端口：**8055**（在 `application.yml` 中为 `server.port: 8055`）。  
首次启动会自动插入工艺知识库、模型配置、工艺路径、用户与角色等基础数据。

## 6. 验证

- 浏览器访问：`http://localhost:8055/api/knowledge`（或其它 `/api/...` 接口）
- 若返回 JSON 数据（或列表），说明数据库与后端已部署成功

## 常见问题

- **找不到 `mysql` 命令**：将 MySQL 的 `bin` 目录加入系统 PATH，或在本机找到 `mysql.exe` 后，在 `scripts\create-db.bat` 里把 `MYSQL_CMD` 改为该路径。
- **连接被拒绝**：确认 MySQL 服务已启动，且端口为 3306（若不同，需在 `application.yml` 或 `application-dev.yml` 的 `url` 中修改端口）。
- **Access denied**：检查 `application.yml` 或 `application-dev.yml` 中的 `username`、`password` 是否与本机 MySQL 一致。
