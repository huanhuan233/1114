-- 创建 process_agent 数据库（MySQL 5.7+）
CREATE DATABASE IF NOT EXISTS process_agent
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 确认
SELECT 'Database process_agent created.' AS result;
SHOW DATABASES LIKE 'process_agent';
