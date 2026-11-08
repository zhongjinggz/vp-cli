package vpcli.shell;

import java.io.IOException;

/**
 * 对应 src/main/python/vp_export.py 的命令行入口。
 */
public final class VpExport {

    private VpExport() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CLIParams params = new CLIParams(args).parse();

        if (params.isInvalid()) {
            System.err.println(params.errorMessage());
            System.exit(2);
        }

        params.setAction(VpCliCommon.VALUE_EXPORT);

        VpCliCommon.runVpPlugin(params);
    }
}
