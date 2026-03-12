#!/bin/bash
# 从项目根目录启动：转发到 prototype 目录执行
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/prototype" && exec ./start.sh
