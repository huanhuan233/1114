#!/bin/bash
# 工艺智能体前后端后台启动脚本
# 用法: ./start.sh  或  bash start.sh

cd "$(dirname "$0")"
mkdir -p logs

# 若 8055 已被占用则先释放，避免 "Port 8055 was already in use"
pid_8055=$(lsof -ti:8055 2>/dev/null)
if [ -n "$pid_8055" ]; then
  kill -9 $pid_8055 2>/dev/null && echo "已释放端口 8055 (PID $pid_8055)"
  sleep 2
fi

# 后端 (Spring Boot + MySQL, 端口 8055)，需先建好 process_agent 库
if ! pgrep -f "process-agent-backend" >/dev/null 2>&1; then
  ( cd backend && nohup ./mvnw spring-boot:run > ../logs/backend.log 2>&1 & )
  echo "后端已后台启动（MySQL），端口 8055，日志 logs/backend.log"
else
  echo "后端已在运行，跳过"
fi

# 前端 (Vite, 端口 5355)
if ! pgrep -f "node.*vite" >/dev/null 2>&1; then
  ( cd frontend && nohup node node_modules/vite/bin/vite.js > ../logs/frontend.log 2>&1 & )
  echo "前端已后台启动，端口 5355，日志 logs/frontend.log"
else
  echo "前端已在运行，跳过"
fi

echo ""
echo "访问: http://192.168.0.97:5355/"
echo "查看日志: tail -f logs/backend.log  或  tail -f logs/frontend.log"
