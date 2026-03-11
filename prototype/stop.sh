#!/bin/bash
# 停止前后端
cd "$(dirname "$0")"

pkill -f "process-agent-backend" 2>/dev/null && echo "已停止后端" || true
pid=$(lsof -ti:8055 2>/dev/null); [ -n "$pid" ] && kill -9 $pid 2>/dev/null && echo "已释放端口 8055" || echo "后端未在运行"

pkill -f "node.*vite" 2>/dev/null && echo "已停止前端" || true
pid=$(lsof -ti:5355 2>/dev/null); [ -n "$pid" ] && kill -9 $pid 2>/dev/null && echo "已释放端口 5355" || echo "前端未在运行"
