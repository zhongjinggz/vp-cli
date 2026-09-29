#!/usr/bin/env python3

import sys
from vp_cli_common import CLIParams, run_vp_plugin, VALUE_TREE

def main():
    params = CLIParams(sys.argv[1:]).parse()

    if params.is_invalid():
        print(params.error_message, file=sys.stderr)
        sys.exit(2)

    params.action = VALUE_TREE

    run_vp_plugin(params)

if __name__ == "__main__":
    main()