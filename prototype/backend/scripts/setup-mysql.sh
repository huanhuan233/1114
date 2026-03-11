#!/bin/bash
# 创建 process_agent 数据库（MySQL/MariaDB 5.7+）
# 用法: MYSQL_PWD=你的密码 ./setup-mysql.sh  或  ./setup-mysql.sh（会提示输入密码）
set -e
cd "$(dirname "$0")/.."

USER="${MYSQL_USER:-root}"
if [ -z "$MYSQL_PWD" ]; then
  echo -n "MySQL $USER 密码: "
  read -s MYSQL_PWD
  echo
  export MYSQL_PWD
fi

echo "创建数据库 process_agent ..."
mysql -u "$USER" -h "${MYSQL_HOST:-localhost}" -e "
CREATE DATABASE IF NOT EXISTS process_agent
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
SELECT 'Database process_agent ready.' AS result;
SHOW DATABASES LIKE 'process_agent';
"
echo "完成。请确保 application.yml 中 spring.datasource 的 username/password 与上述一致。"
unset MYSQL_PWD
