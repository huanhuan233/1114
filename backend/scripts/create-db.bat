@echo off
chcp 65001 >nul
echo 创建 MySQL 数据库 process_agent ...
echo 请确保 MySQL 已安装并在 PATH 中，或已配置 MYSQL_HOME。
echo.
set MYSQL_CMD=mysql
where mysql >nul 2>nul
if errorlevel 1 (
  if defined MYSQL_HOME set "MYSQL_CMD=%MYSQL_HOME%\bin\mysql"
  if not exist "%MYSQL_CMD%.exe" set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql"
  if not exist "%MYSQL_CMD%.exe" set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql"
)
"%MYSQL_CMD%" -u root -p < "%~dp0create-db.sql"
if errorlevel 1 (
  echo 执行失败，请检查 MySQL 是否启动、用户名密码是否正确。
  pause
  exit /b 1
)
echo.
echo 数据库 process_agent 已就绪。
pause
