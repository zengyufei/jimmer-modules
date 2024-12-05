@echo off
chcp 65001
setlocal

rem 获取当前项目路径
set "project_path=%cd%"
echo %project_path%

rem 获取项目名称
for %%F in ("%project_path%") do set "NAME=%%~nxF"
echo %NAME%

rem 根据参数执行不同操作
if "%~1"=="" (
    echo Usage: %NAME% { start | stop | restart }
    exit /b 1
)

if /i "%~1"=="start" (
    echo Starting %NAME%…
    docker-compose --env-file config.env up -d
    echo Finished!
) else if /i "%~1"=="stop" (
    echo Stopping %NAME%…
    docker-compose down
    echo Finished!
) else if /i "%~1"=="restart" (
    call "%~f0" stop
    call "%~f0" start
) else (
    echo Usage: %NAME% { start | stop | restart }
    exit /b 1
)

endlocal
