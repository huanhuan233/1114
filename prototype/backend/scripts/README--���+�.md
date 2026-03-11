# 创建 process_agent 数据库

MySQL 已安装且服务 **MySQL96** 已在运行，只需创建库即可。

## 方法一：命令行（推荐）

在 PowerShell 或 CMD 中执行（会提示输入 root 密码）：

```powershell
& "C:\Program Files\MySQL\MySQL Server 9.6\bin\mysql.exe" -u root -p -e "CREATE DATABASE IF NOT EXISTS process_agent DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;"
```

或在进入 MySQL 后执行：

```powershell
& "C:\Program Files\MySQL\MySQL Server 9.6\bin\mysql.exe" -u root -p
```

登录成功后输入：

```sql
CREATE DATABASE IF NOT EXISTS process_agent
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
EXIT;
```

## 方法二：用 SQL 文件执行

```powershell
cd "D:\科研i项目\1114\原型\backend\scripts"
& "C:\Program Files\MySQL\MySQL Server 9.6\bin\mysql.exe" -u root -p < create-db.sql
```

## 方法三：MySQL Workbench / Navicat 等图形工具

连接本机 MySQL 后，在 SQL 窗口执行：

```sql
CREATE DATABASE IF NOT EXISTS process_agent
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

---

创建完成后，在 `backend/src/main/resources/application.yml` 中把数据库密码改为你的 root 密码，然后运行：

```powershell
cd backend
mvn spring-boot:run
```

首次启动时 Spring Boot 会按实体自动建表。
