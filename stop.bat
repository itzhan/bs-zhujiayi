@echo off
chcp 65001 >nul 2>&1

:: ============================================================
:: 酒店预订管理系统 - 一键停止脚本 (Windows)
:: ============================================================

echo.
echo 正在停止酒店预订管理系统所有服务...
echo.

:: 通过窗口标题停止服务
taskkill /fi "WINDOWTITLE eq 后端服务*" /f >nul 2>&1
echo [✓] 停止后端服务

taskkill /fi "WINDOWTITLE eq 管理端*" /f >nul 2>&1
echo [✓] 停止管理端

taskkill /fi "WINDOWTITLE eq 前端*" /f >nul 2>&1
echo [✓] 停止前端

:: 通过端口清理
for %%p in (8088 5173 8848) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%%p ^| findstr LISTENING') do (
        taskkill /pid %%a /f >nul 2>&1
        echo [✓] 清理端口 %%p 上的进程
    )
)

echo.
echo 所有服务已停止
echo.
pause
