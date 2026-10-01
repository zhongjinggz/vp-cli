#!/usr/bin/env python3

import os
import subprocess
import sys

VP_APP = "/Applications/Visual Paradigm.app/Contents/Resources/app"

KEY_PROJECT = "-project"
KEY_ACTION = "-action"
KEY_TARGET = "-target"
KEY_PATH = "-path"

VALUE_IMPORT = "import"
VALUE_EXPORT = "export"
VALUE_TREE = "tree"
VALUE_LIST_DIAGRAMS = "list-diagrams"
VALUE_ALL = "all"

VALUE_UNSET = "unset"  # 命令行中根本没有该参数
VALUE_SET = "set"  # 存在该独立参数
VALUE_NON = "non_value"  # 该参数存在但缺少值


def is_key_value_param(key):
    return key in [KEY_PROJECT
        , KEY_ACTION
        , KEY_TARGET
        , KEY_PATH]


def is_key_only_param(key):
    return key == KEY_LIST


class CLIParams:

    def __init__(self, args):
        # 复制入参，避免无意中修改
        self.args = list(args) if args else []
        self.index = 0

        self.project = VALUE_UNSET
        self.action = VALUE_UNSET
        self.path = VALUE_UNSET
        self.list = VALUE_UNSET
        self.target = VALUE_ALL  # -target 的默认值是 all

        self.error_message = ""

    def parse(self):
        if not self.args:
            raise RuntimeError("== Bug: no args! ==")

        while self.index < len(self.args):
            key, value = self._parse_value()
            if self.is_invalid():
                break

            # TODO 可以根据 KEY 名称推断属性名，从而更简练
            if key == KEY_PROJECT:
                self.project = value
            elif key == KEY_ACTION:
                self.action = value
            elif key == KEY_TARGET:
                self.target = value
            elif key == KEY_PATH:
                self.path = value
            elif key == KEY_LIST:
                self.list = value
            else:
                raise RuntimeError(
                    "== Bug: unknown argument {0} should have been handled in '_parse_value()' == "
                    .format(key))

        return self

    def _parse_value(self):
        key = self.args[self.index]
        value = VALUE_UNSET
        if is_key_value_param(key):
            if self.index + 1 < len(self.args):
                value = self.args[self.index + 1]
                self.index += 1
            else:
                value = VALUE_NON
                self._set_error("Error: Missing value for " + key)
        elif is_key_only_param(key):
            value = VALUE_SET
        else:
            self._set_error("Unknown argument: " + key)

        self.index += 1

        return key, value

    def _set_error(self, message):
        self.error_message = message

    def is_invalid(self):
        return bool(self.error_message)


def run_vp_plugin(params: CLIParams):
    project = os.path.realpath(params.project)
    output_path = os.path.realpath(params.path)

    app_bin = os.path.join(VP_APP, "bin")
    os.chdir(app_bin)

    cmd = [
        "java",
        "--add-exports", "java.desktop/com.apple.eawt=ALL-UNNAMED",
        "-Xms256m",
        "-Xmx768m",
        "-Djava.awt.headless=true",
        "-cp", ".:{0}/lib/*:{0}/ormlib/*".format(VP_APP),
        "com.vp.cmd.Plugin",
        "-pluginid", "plugins.vpcli",
        "-project", project,
        "-pluginargs",
        "-action", params.action,
        "-path", output_path,
        "-target", params.target
    ]
    sys.exit(subprocess.call(cmd))
