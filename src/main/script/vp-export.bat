@echo off
rem 等价的 Windows 批处理版本（对应 vp-export 脚本）
java -cp "%~dp0..\classes" vpcli.shell.VpExport %*
