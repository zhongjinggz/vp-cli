package vpcli.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对应 src/main/python/vp_cli_common.py。
 *
 * <p>提供命令行常量与 {@link #runVpPlugin(CLIParams) 调用 VP 插件} 的过程，
 * 供 {@code vp-shell}（命令行入口）复用。
 */
public final class VpCliCommon {

    static final String VP_APP = loadVpApp();

    static final String KEY_PROJECT = "-project";
    static final String KEY_ACTION = "-action";
    static final String KEY_TARGET = "-target";
    static final String KEY_PATH = "-path";
    static final String KEY_LIST = "-list";

    static final String VALUE_IMPORT = "import";
    static final String VALUE_EXPORT = "export";
    static final String VALUE_TREE = "tree";
    static final String VALUE_LIST_DIAGRAMS = "list-diagrams";
    static final String VALUE_ALL = "all";

    static final String VALUE_UNSET = "unset";   // 命令行中根本没有该参数
    static final String VALUE_SET = "set";       // 存在该独立参数
    static final String VALUE_NON = "non_value"; // 该参数存在但缺少值

    private VpCliCommon() {
    }

    private static String loadVpApp() {
        Properties props = new Properties();
        try (InputStream in = VpCliCommon.class.getClassLoader()
                .getResourceAsStream("vpcli.properties")) {
            if (in != null) {
                props.load(in);
                return props.getProperty("vp.app")
                        .replace("\"", "")
                        .trim();
            }
        } catch (IOException e) {
            // 忽略，回退到默认路径
        }
        return "/Applications/Visual Paradigm.app/Contents/Resources/app";
    }

    /**
     * 对应 run_vp_plugin(params)：切换到 VP 安装目录的 bin，启动 java 加载
     * {@code com.vp.cmd.Plugin} 插件并转发命令行参数，以插件的退出码结束本进程。
     *
     * @param params 已解析的命令行参数
     */
    public static void runVpPlugin(CLIParams params) throws IOException, InterruptedException {
        String project = new File(params.getProject()).getCanonicalPath();
        String outputPath = new File(params.getPath()).getCanonicalPath();

        File appBin = new File(VP_APP, "bin");

        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("--add-exports");
        command.add("java.desktop/com.apple.eawt=ALL-UNNAMED");
        command.add("-Xms256m");
        command.add("-Xmx768m");
        command.add("-Djava.awt.headless=true");
        command.add("-Dvpcli.log.profile=prod");
        command.add("-cp");
        command.add(".:" + VP_APP + "/lib/*:" + VP_APP + "/ormlib/*");
        command.add("com.vp.cmd.Plugin");
        command.add("-pluginid");
        command.add("vpcli");
        command.add("-project");
        command.add(project);
        command.add("-pluginargs");
        command.add("-action");
        command.add(params.getAction());
        command.add("-path");
        command.add(outputPath);
        command.add("-target");
        command.add(params.getTarget());

        Process process = new ProcessBuilder(command)
            .directory(appBin)
            .inheritIO()
            .start();
        int exitCode = process.waitFor();
        System.exit(exitCode);
    }
}
