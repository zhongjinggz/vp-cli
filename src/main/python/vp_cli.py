#!/usr/bin/env python3

import os
import subprocess
import sys

'''
'''

VP_APP = "/Applications/Visual Paradigm.app/Contents/Resources/app"


class ParamValue:
    """封装单个参数值，提供流畅的条件判断 DSL。"""

    def __init__(self, text):
        self.text = text

    def is_unset(self):
        return self.text == CLIParams.VALUE_UNSET

    def is_non_value(self):
        return self.text == CLIParams.VALUE_NON

    def is_set(self):
        return self.text == CLIParams.VALUE_SET

    def __str__(self):
        return self.text


class CLIParams:
    KEY_ACTION = "-action"
    KEY_TARGET = "-target"
    KEY_PATH = "-path"
    KEY_LIST = "-list"
    KEY_PROJECT = "-project"

    VALUE_IMPORT = "import"
    VALUE_EXPORT = "export"
    VALUE_ALL = "all"

    VALUE_UNSET = "unset"    # 命令行中根本没有该参数
    VALUE_SET = "set"        # 存在该独立参数
    VALUE_NON = "non_value"  # 该参数存在但缺少值

    def __init__(self, args):
        # 复制入参，避免无意中修改
        self.args = list(args) if args else []

        # key/value 形式的参数
        self.key_value_params = {
            self.KEY_ACTION: self.VALUE_UNSET,
            self.KEY_TARGET: self.VALUE_UNSET,
            self.KEY_PATH: self.VALUE_UNSET,
            self.KEY_PROJECT: self.VALUE_UNSET,
        }
        # 不带值形式的参数
        self.key_only_params = {
            self.KEY_LIST: self.VALUE_UNSET,
        }

        self.error_message = ""

    def parse(self):
        self.acquire_params()

    def acquire_params(self):
        if not self.args:
            self.set_error(
                "Usage: -project <vp_project> -action <import|export> -path <file_or_folder_path>")
            return

        index = 0
        while index < len(self.args):
            key = self.args[index]

            if key in self.key_value_params:
                index = self._parse_value(index)
            elif key in self.key_only_params:
                self.key_only_params[key] = self.VALUE_SET
                index += 1
            else:
                self.set_error("Unknown argument: " + key)
                return

            if self.is_invalid():
                return

    def _parse_value(self, index):
        key = self.args[index]
        if index + 1 < len(self.args):
            self.key_value_params[key] = self.args[index + 1]
            return index + 2
        else:
            self.key_value_params[key] = self.VALUE_NON
            self.set_error("Error: Missing value for " + key)
            return index + 1

    # ---- 访问器 ----
    def action(self):
        return ParamValue(self.key_value_params[self.KEY_ACTION])

    def target(self):
        return ParamValue(self.key_value_params[self.KEY_TARGET])

    def path(self):
        return ParamValue(self.key_value_params[self.KEY_PATH])

    def project(self):
        return ParamValue(self.key_value_params[self.KEY_PROJECT])

    def list(self):
        return ParamValue(self.key_only_params[self.KEY_LIST])

    # ---- 错误处理 ----
    def set_error(self, message):
        self.error_message = message

    def is_invalid(self):
        return bool(self.error_message)


def parse_args():
    """解析命令行参数并生成 CLIParams。"""
    params = CLIParams(sys.argv[1:])
    params.parse()
    return params


def main():
    params = parse_args()

    if params.is_invalid():
        print(params.error_message, file=sys.stderr)
        sys.exit(2)

    project = os.path.realpath(params.project().text)
    output_path = os.path.realpath(params.path().text)
    target = params.target().text if not params.target().is_unset() else CLIParams.VALUE_ALL

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
        "-action", params.action().text,
        "-path", output_path,
        "-target", target,
    ]

    sys.exit(subprocess.call(cmd))


if __name__ == "__main__":
    main()