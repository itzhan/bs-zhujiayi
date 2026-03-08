#!/bin/bash
# ============================================================
# 酒店预订管理系统 - 一键停止脚本 (Mac/Linux)
# ============================================================

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$PROJECT_DIR/.logs"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo ""
echo -e "${YELLOW}正在停止酒店预订管理系统所有服务...${NC}"
echo ""

# 停止 tail 监控进程
for pidfile in "$LOG_DIR/tail_backend.pid" "$LOG_DIR/tail_admin.pid" "$LOG_DIR/tail_frontend.pid"; do
    if [ -f "$pidfile" ]; then
        PID=$(cat "$pidfile")
        kill -9 "$PID" 2>/dev/null && echo -e "${GREEN}[✓]${NC} 停止日志监控进程 $PID"
        rm -f "$pidfile"
    fi
done

# 通过 PID 文件停止服务
for service in backend admin frontend; do
    pidfile="$LOG_DIR/$service.pid"
    if [ -f "$pidfile" ]; then
        PID=$(cat "$pidfile")
        if kill -0 "$PID" 2>/dev/null; then
            kill "$PID" 2>/dev/null
            sleep 1
            kill -9 "$PID" 2>/dev/null || true
            echo -e "${GREEN}[✓]${NC} 停止 $service (PID: $PID)"
        fi
        rm -f "$pidfile"
    fi
done

# 通过端口号双重清理
for PORT in 8088 5173 8848; do
    PID=$(lsof -ti:$PORT 2>/dev/null || true)
    if [ -n "$PID" ]; then
        kill -9 $PID 2>/dev/null
        echo -e "${GREEN}[✓]${NC} 清理端口 $PORT 上的进程 $PID"
    fi
done

echo ""
echo -e "${GREEN}所有服务已停止${NC}"
echo ""
