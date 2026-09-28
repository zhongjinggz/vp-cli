#!/usr/bin/env python3

import sys

from vp_cli import CLIParams, run_plugin, VALUE_EXPORT


def main():
    params = CLIParams(sys.argv[1:]).parse()

    if params.is_invalid():
        print(params.error_message, file=sys.stderr)
        sys.exit(2)

    params.action = VALUE_EXPORT

    run_plugin(params)


if __name__ == "__main__":
    main()