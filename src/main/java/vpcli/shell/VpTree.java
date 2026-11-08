package vpcli.shell;

import java.io.IOException;

/**
 * 对应 src/main/python/vp_tree.py 的命令行入口。
 */
public final class VpTree {

    private VpTree() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CLIParams params = new CLIParams(args).parse();

        if (params.isInvalid()) {
            System.err.println(params.errorMessage());
            System.exit(2);
        }

        params.setAction(VpCliCommon.VALUE_TREE);

        VpCliCommon.runVpPlugin(params);
    }
}
