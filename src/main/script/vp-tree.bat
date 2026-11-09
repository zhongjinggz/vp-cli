@echo off
rem 等价的 Windows 批处理版本（对应 vp-tree 脚本）
java -cp "%~dp0..\classes;%~dp0..\lib\picocli-4.7.6.jar" vpcli.shell.VPCLIShell tree %*
