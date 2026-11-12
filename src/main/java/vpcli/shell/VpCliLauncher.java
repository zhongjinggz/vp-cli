package vpcli.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 切换到 VP 安装目录的 bin，启动 java 加载 {@code com.vp.cmd.Plugin} 插件并转发命令行参数，以插件的退出码
 * 结束本进程。
 */
final class VpCliLauncher {

    private static final String VP_APP_PATH =
        "/Applications/Visual Paradigm.app/Contents/Resources/app";

    private VpCliLauncher() {
    }

    static int launch(String projectPath, String... pluginArgs) throws IOException, InterruptedException {
        String canonicalProjectPath = new File(projectPath).getCanonicalPath();
        String vpAppPath = getVpAppPath();


        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("--add-exports");
        command.add("java.desktop/com.apple.eawt=ALL-UNNAMED");
        command.add("-Xms256m");
        command.add("-Xmx768m");
        command.add("-Djava.awt.headless=true");
        command.add("-cp");
        command.add(".:" + vpAppPath + "/lib/*:" + vpAppPath + "/ormlib/*");
        command.add("com.vp.cmd.Plugin");
        command.add("-pluginid");
        command.add("com.github.zhongjinggz.vpcli");
        command.add("-project");
        command.add(canonicalProjectPath);
        command.add("-pluginargs");
        command.addAll(Arrays.asList(pluginArgs));

        Process process = new ProcessBuilder(command)
            .directory(new File(vpAppPath, "bin"))
            .inheritIO()
            .start();
        return process.waitFor();
    }

    private static String getVpAppPath() {
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
        return VP_APP_PATH;
    }
}
