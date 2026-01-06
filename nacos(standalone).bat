@echo off
chcp 65001 # 防止中文乱码
title 一键启动多个程序


@echo off
start cmd /k "neo4j.bat console"

@echo off
start cmd /k "cd /d E:\softofcomputer\nacos\bin && startup.cmd -m standalone"

@echo off
start cmd /k "cd /d E:\softofcomputer\redis && redis-server.exe"




