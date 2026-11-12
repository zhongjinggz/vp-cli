package vpcli.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 承载参数分层的纯值对象。
 *
 * <p>{@code project} 是 VP 进程参数，{@code pluginArgs} 是 {@code -pluginargs}
 * 之后的插件参数（形如 {@code List.of("export","DiagramA","out")}）。
 */
final class PluginArgs {
    final String project;
    final List<String> pluginArgs;

    private PluginArgs(String project, List<String> pluginArgs) {
        this.project = project;
        this.pluginArgs = pluginArgs;
    }

    static PluginArgs of(String project, List<String> pluginArgs) {
        return new PluginArgs(project, pluginArgs);
    }
}

/**
 * 切换到 VP 安装目录的 bin，启动 java 加载 {@code com.vp.cmd.Plugin} 插件并转发命令行参数，以插件的退出码
 * 结束本进程。
 */
final class VpCliLauncher {

    private static final String DEFAULT_VP_APP =
        "/Applications/Visual Paradigm.app/Contents/Resources/app";

    private VpCliLauncher() {
    }

    static int launch(String projectPath, String... pluginArgs2) throws IOException, InterruptedException {
        String project = new File(projectPath).getCanonicalPath();
        String vpApp = loadVpApp();

        File appBin = new File(vpApp, "bin");

        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("--add-exports");
        command.add("java.desktop/com.apple.eawt=ALL-UNNAMED");
        command.add("-Xms256m");
        command.add("-Xmx768m");
        command.add("-Djava.awt.headless=true");
        command.add("-cp");
        command.add(".:" + vpApp + "/lib/*:" + vpApp + "/ormlib/*");
        command.add("com.vp.cmd.Plugin");
        command.add("-pluginid");
        command.add("com.github.zhongjinggz.vpcli");
        command.add("-project");
        command.add(project);
        command.add("-pluginargs");
//        command.addAll(pluginArgTokens(args.pluginArgs));
        command.addAll(Arrays.asList(pluginArgs2));

        Process process = new ProcessBuilder(command)
            .directory(appBin)
            .inheritIO()
            .start();
        return process.waitFor();
    }

    // 按旧实现固定顺序 -action -> -path  展开插件参数（纯函数，便于单测）
    static List<String> pluginArgTokens(List<String> pluginArgs) {
        List<String> tokens = new ArrayList<>();
        String action = pluginArgs.get(0);
        tokens.add("-action");
        tokens.add(action);

        if (pluginArgs.size() >= 2 && "export".equals(action)) {
            String path = pluginArgs.get(1);

            tokens.add("-path");
            tokens.add(path);
        }
        return tokens;
    }

    private static String loadVpApp() {
        Properties props = new Properties();
        try (InputStream in = VpCliLauncher.class.getClassLoader()
            .getResourceAsStream("vpcli.properties")) {
            if (in != null) {
                props.load(in);
                String vpApp = props.getProperty("vp.app");
                if (vpApp != null && !vpApp.isBlank()) {
                    return vpApp.replace("\"", "").trim();
                }
            }
        } catch (IOException e) {
            // 忽略，回退到默认路径
        }
        return DEFAULT_VP_APP;
    }
}
