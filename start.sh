#!/bin/bash
# ============================================================
# 酒店预订管理系统 - 一键启动脚本 (Mac/Linux)
# ============================================================

set -e

# ==================== 配置变量 ====================
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="hotel_booking"
DB_USER="root"
DB_PASS="123456"
BACKEND_PORT=8088
FRONTEND_PORT=5173
ADMIN_PORT=8848
LOG_DIR="$PROJECT_DIR/.logs"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

banner() {
    echo ""
    echo -e "${CYAN}╔══════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║       🏨 酒店预订管理系统 - 一键启动            ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════╝${NC}"
    echo ""
}

log_info()  { echo -e "${GREEN}[✓]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
log_error() { echo -e "${RED}[✗]${NC} $1"; }
log_step()  { echo -e "${BLUE}[→]${NC} $1"; }

# ==================== 环境检查 ====================
check_env() {
    log_step "检查基础环境..."

    # Java
    if ! command -v java &>/dev/null; then
        log_error "未找到 Java，请安装 JDK 17+"
        echo "  安装指引: brew install openjdk@17"
        exit 1
    fi
    log_info "Java: $(java -version 2>&1 | head -1)"

    # Maven
    if ! command -v mvn &>/dev/null; then
        log_error "未找到 Maven，请安装 Maven 3.6+"
        echo "  安装指引: brew install maven"
        exit 1
    fi
    log_info "Maven: $(mvn -version 2>&1 | head -1)"

    # Node.js
    if ! command -v node &>/dev/null; then
        log_error "未找到 Node.js，请安装 Node.js 16+"
        echo "  安装指引: brew install node"
        exit 1
    fi
    log_info "Node.js: $(node -v)"

    # pnpm
    if ! command -v pnpm &>/dev/null; then
        log_warn "未找到 pnpm，正在安装..."
        npm install -g pnpm
    fi
    log_info "pnpm: $(pnpm -v)"

    # MySQL
    if ! command -v mysql &>/dev/null; then
        log_error "未找到 MySQL 客户端"
        echo "  安装指引: brew install mysql"
        exit 1
    fi
    log_info "MySQL 客户端就绪"
}

# ==================== MySQL 检查 ====================
check_mysql() {
    log_step "检查 MySQL 数据库..."

    # 检查 MySQL 服务
    if ! mysqladmin ping -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --silent 2>/dev/null; then
        log_warn "MySQL 服务未运行，尝试启动..."
        if [[ "$(uname)" == "Darwin" ]]; then
            brew services start mysql 2>/dev/null || true
        else
            sudo systemctl start mysql 2>/dev/null || sudo service mysql start 2>/dev/null || true
        fi
        sleep 3
        if ! mysqladmin ping -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --silent 2>/dev/null; then
            log_error "无法连接 MySQL，请手动启动 MySQL 服务"
            exit 1
        fi
    fi
    log_info "MySQL 服务运行中"

    # 检查数据库是否存在
    DB_EXISTS=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='$DB_NAME'" 2>/dev/null | grep -c "$DB_NAME" || true)
    TABLE_COUNT=0
    if [ "$DB_EXISTS" -gt 0 ]; then
        TABLE_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 -e "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='$DB_NAME'" -sN 2>/dev/null || echo "0")
    fi

    if [ "$DB_EXISTS" -eq 0 ] || [ "$TABLE_COUNT" -eq 0 ]; then
        log_warn "数据库不存在或为空，正在初始化..."
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 < "$PROJECT_DIR/sql/init.sql"
        log_info "表结构导入完成"
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 < "$PROJECT_DIR/sql/data.sql"
        log_info "测试数据导入完成"
    else
        log_info "数据库 $DB_NAME 已存在 ($TABLE_COUNT 张表)"
    fi
}

# ==================== 依赖检查 ====================
check_deps() {
    log_step "检查项目依赖..."

    # 后端
    if [ ! -d "$PROJECT_DIR/backend/target/classes" ]; then
        log_warn "后端未编译，正在编译..."
        cd "$PROJECT_DIR/backend"
        mvn compile -q 2>&1 || { log_error "后端编译失败"; exit 1; }
        log_info "后端编译完成"
    else
        log_info "后端已编译"
    fi

    # 管理端
    if [ ! -d "$PROJECT_DIR/admin/node_modules" ]; then
        log_warn "管理端依赖未安装，正在安装..."
        cd "$PROJECT_DIR/admin"
        pnpm install 2>&1 || { log_error "管理端依赖安装失败"; exit 1; }
        log_info "管理端依赖安装完成"
    else
        log_info "管理端依赖已安装"
    fi

    # 前端
    if [ ! -d "$PROJECT_DIR/frontend/node_modules" ]; then
        log_warn "前端依赖未安装，正在安装..."
        cd "$PROJECT_DIR/frontend"
        pnpm install 2>&1 || { log_error "前端依赖安装失败"; exit 1; }
        log_info "前端依赖安装完成"
    else
        log_info "前端依赖已安装"
    fi
}

# ==================== 端口检查 ====================
check_ports() {
    log_step "检查端口占用..."
    for PORT in $BACKEND_PORT $FRONTEND_PORT $ADMIN_PORT; do
        PID=$(lsof -ti:$PORT 2>/dev/null || true)
        if [ -n "$PID" ]; then
            log_warn "端口 $PORT 被进程 $PID 占用"
            read -p "  是否终止该进程？(y/n) " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                kill -9 $PID 2>/dev/null || true
                log_info "已终止进程 $PID"
            else
                log_error "端口 $PORT 被占用，请手动处理"
                exit 1
            fi
        fi
    done
    log_info "所有端口可用"
}

# ==================== 启动服务 ====================
start_services() {
    mkdir -p "$LOG_DIR"

    log_step "启动后端服务 (端口: $BACKEND_PORT)..."
    cd "$PROJECT_DIR/backend"
    mvn spring-boot:run -q > "$LOG_DIR/backend.log" 2>&1 &
    echo $! > "$LOG_DIR/backend.pid"

    log_step "启动管理端 (端口: $ADMIN_PORT)..."
    cd "$PROJECT_DIR/admin"
    pnpm dev > "$LOG_DIR/admin.log" 2>&1 &
    echo $! > "$LOG_DIR/admin.pid"

    log_step "启动前端 (端口: $FRONTEND_PORT)..."
    cd "$PROJECT_DIR/frontend"
    pnpm dev > "$LOG_DIR/frontend.log" 2>&1 &
    echo $! > "$LOG_DIR/frontend.pid"
}

# ==================== 等待就绪 ====================
wait_ready() {
    log_step "等待服务就绪..."

    # 等待后端
    for i in $(seq 1 60); do
        if curl -s "http://localhost:$BACKEND_PORT/api/announcements/published" >/dev/null 2>&1; then
            log_info "后端服务就绪"
            break
        fi
        if [ $i -eq 60 ]; then
            log_error "后端启动超时，请查看日志: $LOG_DIR/backend.log"
            exit 1
        fi
        sleep 2
    done

    # 等待前端 (通常较快)
    sleep 5
    log_info "前端服务就绪"
    log_info "管理端服务就绪"
}

# ==================== 输出信息 ====================
print_info() {
    echo ""
    echo -e "${CYAN}══════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  所有服务已启动！${NC}"
    echo -e "${CYAN}══════════════════════════════════════════════════${NC}"
    echo ""
    echo -e "  ${BLUE}后端 API:${NC}    http://localhost:$BACKEND_PORT"
    echo -e "  ${BLUE}用户端前端:${NC}  http://localhost:$FRONTEND_PORT"
    echo -e "  ${BLUE}管理端后台:${NC}  http://localhost:$ADMIN_PORT"
    echo ""
    echo -e "  ${YELLOW}测试账号:${NC}"
    echo -e "    管理员:   admin / admin123"
    echo -e "    普通用户: zhangsan / 123456"
    echo -e "    普通用户: lisi / 123456"
    echo -e "    普通用户: wangwu / 123456"
    echo ""
    echo -e "  ${YELLOW}日志目录:${NC} $LOG_DIR/"
    echo -e "  ${YELLOW}停止服务:${NC} ./stop.sh"
    echo ""
    echo -e "${CYAN}══════════════════════════════════════════════════${NC}"
}

# ==================== 实时日志输出 ====================
tail_logs() {
    # 启动日志尾随
    tail -f "$LOG_DIR/backend.log" 2>/dev/null | while read line; do echo -e "${RED}[后端]${NC} $line"; done &
    echo $! > "$LOG_DIR/tail_backend.pid"
    tail -f "$LOG_DIR/admin.log" 2>/dev/null | while read line; do echo -e "${BLUE}[管理端]${NC} $line"; done &
    echo $! > "$LOG_DIR/tail_admin.pid"
    tail -f "$LOG_DIR/frontend.log" 2>/dev/null | while read line; do echo -e "${GREEN}[前端]${NC} $line"; done &
    echo $! > "$LOG_DIR/tail_frontend.pid"
}

# ==================== 清理函数 ====================
cleanup() {
    echo ""
    log_warn "正在停止所有服务..."
    "$PROJECT_DIR/stop.sh" 2>/dev/null || true
    exit 0
}

# ==================== 主流程 ====================
trap cleanup SIGINT SIGTERM

banner
check_env
check_mysql
check_deps
check_ports
start_services
wait_ready
print_info
tail_logs

# 保持脚本运行，等待 Ctrl+C
wait
