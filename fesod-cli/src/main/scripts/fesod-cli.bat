@echo off
setlocal enabledelayedexpansion

REM ============================================================================
REM Licensed to the Apache Software Foundation (ASF) under one
REM or more contributor license agreements.  See the NOTICE file
REM distributed with this work for additional information
REM regarding copyright ownership.  The ASF licenses this file
REM to you under the Apache License, Version 2.0 (the
REM "License"); you may not use this file except in compliance
REM with the License.  You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing,
REM software distributed under the License is distributed on an
REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM KIND, either express or implied.  See the License for the
REM specific language governing permissions and limitations
REM under the License.
REM ============================================================================

REM ============================================================================
REM Apache Fesod CLI Launcher Script for Windows
REM Supports: JDK 8+
REM ============================================================================

REM 脚本所在目录
set SCRIPT_DIR=%~dp0

REM Fesod CLI 主目录
if not defined FESOD_HOME (
    set FESOD_HOME=%SCRIPT_DIR%..
)

REM 构建 CLASSPATH
REM Fesod modules
set CLASSPATH=%FESOD_HOME%\lib\*
REM Third-party dependencies
set CLASSPATH=%CLASSPATH%;%FESOD_HOME%\lib\ext\*
REM Configuration directory
set CLASSPATH=%CLASSPATH%;%FESOD_HOME%\conf

REM 检查 lib 目录是否存在
if not exist "%FESOD_HOME%\lib" (
    echo Error: Cannot find lib directory at %FESOD_HOME%\lib
    exit /b 1
)

REM 查找 Java
set JAVA_CMD=

REM 1. 检查 JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set JAVA_CMD=%JAVA_HOME%\bin\java.exe
        goto :java_found
    )
)

REM 2. 检查 PATH
where java >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set JAVA_CMD=java
    goto :java_found
)

REM 3. 检查常见安装路径
for %%d in (
    "C:\Program Files\Java\jdk-8"
    "C:\Program Files\Java\jdk1.8.0_*"
    "C:\Program Files\Java\jdk-11"
    "C:\Program Files\OpenJDK\jdk-8"
    "C:\Program Files\Eclipse Adoptium\jdk-8*"
) do (
    if exist "%%~d\bin\java.exe" (
        set JAVA_CMD=%%~d\bin\java.exe
        goto :java_found
    )
)

:java_not_found
echo Error: Java is not installed or not in PATH
echo.
echo Please install Java 8 or higher:
echo   - Download from: https://adoptium.net/
echo   - Or install via Chocolatey: choco install openjdk8
echo.
echo Or set JAVA_HOME environment variable:
echo   set JAVA_HOME=C:\Path\To\JDK
exit /b 1

:java_found

REM 检查 Java 版本
for /f "tokens=3" %%g in ('"%JAVA_CMD%" -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)
set JAVA_VERSION=%JAVA_VERSION:"=%

REM 提取主版本号
for /f "delims=." %%a in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%a
if "%JAVA_MAJOR%" equ "1" (
    for /f "tokens=2 delims=." %%a in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%a
)

if %JAVA_MAJOR% lss 8 (
    echo Error: Java 8 or higher is required
    echo Current Java version: %JAVA_VERSION%
    echo Java command: %JAVA_CMD%
    exit /b 1
)

REM JVM 参数
if not defined FESOD_JAVA_OPTS (
    set JAVA_OPTS=-Xms128m -Xmx1g
) else (
    set JAVA_OPTS=%FESOD_JAVA_OPTS%
)

REM 日志配置
if exist "%FESOD_HOME%\conf\logback.xml" (
    set JAVA_OPTS=%JAVA_OPTS% -Dlogback.configurationFile=%FESOD_HOME%\conf\logback.xml
)

REM 字符编码
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8

REM 执行命令
"%JAVA_CMD%" %JAVA_OPTS% -cp "%CLASSPATH%" org.apache.fesod.cli.FesodCli %*
exit /b %ERRORLEVEL%
