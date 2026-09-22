#!/usr/bin/env python3

import os
import subprocess
import sys

APP = "/Applications/Visual Paradigm.app/Contents/Resources/app"
PROJECT = "UNDEFINE"
OUTPUT_PATH = "UNDEFINE"

args = sys.argv[1:]
i = 0
while i < len(args):
    arg = args[i]
    if arg == "-project":
        if i + 1 < len(args):
            PROJECT = args[i + 1]
        i += 2
    elif arg == "-path":
        if i + 1 < len(args):
            OUTPUT_PATH = args[i + 1]
        i += 2
    else:
        i += 1

PROJECT = os.path.realpath(PROJECT)
OUTPUT_PATH = os.path.realpath(OUTPUT_PATH)

app_bin = os.path.join(APP, "bin")
os.chdir(app_bin)

cmd = [
    "java",
    "--add-exports", "java.desktop/com.apple.eawt=ALL-UNNAMED",
    "-Xms256m", "-Xmx768m",
    "-Djava.awt.headless=true",
    "-cp", ".:{0}/lib/*:{0}/ormlib/*".format(APP),
    "com.vp.cmd.Plugin",
    "-pluginid", "plugins.plantUML",
    "-project", PROJECT,
    "-pluginargs",
    "-action", "export",
    "-path", OUTPUT_PATH,
    "-target", "all",
]

sys.exit(subprocess.call(cmd))