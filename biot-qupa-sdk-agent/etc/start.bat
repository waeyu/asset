@echo off

REM ************************************************************ Change Directory
set curdir=%CD:~0,2%
set dstdir=%~dp0
set dstdir=%dstdir:~0,2%
if %curdir% NEQ %dstdir% (%dstdir%)
cd %~dp0
REM ************************************************************  

java -jar  -Duser.timezone=GMT+08 -Dloader.path=. biot-cupa-sdk-agent-0.9.jar

pause
