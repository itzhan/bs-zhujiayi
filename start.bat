@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

:: ============================================================
:: 酒店预订管理系统 - 一键启动脚本 (Windows)
:: ============================================================

set "PROJECT_DIR=%~dp0"
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=hotel_booking"
set "DB_USER=root"
set "DB_PASS=123456"
set "BACKEND_PORT=8088"
set "FRONTEND_PORT=5173"
set "ADMIN_PORT=8848"

echo.
echo ╔══════════════════════════════════════════════════╗
echo ║       酒店预订管理系统 - 一键启动               ║
echo ╚══════════════════════════════════════════════════╝
echo.

:: ==================== 环境检查 ====================
echo [→] 检查基础环境...

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] 未找到 Java，请安装 JDK 17+
    pause
    exit /b 1
)
echo [✓] Java 已安装

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] 未找到 Maven
    pause
    exit /b 1
)
echo [✓] Maven 已安装

where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [✗] 未找到 Node.js
    pause
    exit /b 1
)
echo [✓] Node.js 已安装

where pnpm >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] 未找到 pnpm，正在安装...
    npm install -g pnpm
)
echo [✓] pnpm 已安装

:: ==================== MySQL 检查 ====================
echo [→] 检查 MySQL 数据库...

mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] MySQL 服务未运行，尝试启动...
    net start mysql >nul 2>&1
    timeout /t 3 /nobreak >nul
)

:: 检查数据库
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 -e "USE %DB_NAME%" >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] 数据库不存在，正在初始化...
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 < "%PROJECT_DIR%sql\init.sql"
    echo [✓] 表结构导入完成
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 < "%PROJECT_DIR%sql\data.sql"
    echo [✓] 测试数据导入完成
) else (
    echo [✓] 数据库已存在
)

:: ==================== 依赖检查 ====================
echo [→] 检查项目依赖...

if not exist "%PROJECT_DIR%backend\target\classes" (
    echo [!] 后端未编译，正在编译...
    cd /d "%PROJECT_DIR%backend"
    mvn compile -q
    if %errorlevel% neq 0 (
        echo [✗] 后端编译失败
        pause
        exit /b 1
    )
)
echo [✓] 后端已编译

if not exist "%PROJECT_DIR%admin\node_modules" (
    echo [!] 管理端依赖未安装...
    cd /d "%PROJECT_DIR%admin"
    pnpm install
)
echo [✓] 管理端依赖已安装

if not exist "%PROJECT_DIR%frontend\node_modules" (
    echo [!] 前端依赖未安装...
    cd /d "%PROJECT_DIR%frontend"
    pnpm install
)
echo [✓] 前端依赖已安装

:: ==================== 启动服务 ====================
echo.
echo [→] 启动所有服务...

:: 后端 (红色窗口)
start "后端服务" cmd /c "color 4F && cd /d "%PROJECT_DIR%backend" && mvn spring-boot:run && pause"

:: 管理端 (蓝色窗口)
start "管理端" cmd /c "color 1F && cd /d "%PROJECT_DIR%admin" && pnpm dev && pause"

:: 前端 (绿色窗口)
start "前端" cmd /c "color 2F && cd /d "%PROJECT_DIR%frontend" && pnpm dev && pause"

:: 等待后端启动
echo [→] 等待服务就绪...
timeout /t 30 /nobreak >nul

echo.
echo ══════════════════════════════════════════════════
echo   所有服务已启动！
echo ══════════════════════════════════════════════════
echo.
echo   后端 API:    http://localhost:%BACKEND_PORT%
echo   用户端前端:  http://localhost:%FRONTEND_PORT%
echo   管理端后台:  http://localhost:%ADMIN_PORT%
echo.
echo   测试账号:
echo     管理员:   admin / admin123
echo     普通用户: zhangsan / 123456
echo     普通用户: lisi / 123456
echo.
echo   停止服务: 运行 stop.bat 或关闭各服务窗口
echo.
echo ══════════════════════════════════════════════════
echo.
pause
