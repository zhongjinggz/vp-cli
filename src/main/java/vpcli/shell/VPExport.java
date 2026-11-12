package vpcli.shell;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 独立导出命令。
 *
 * <p>{@code vp-export -project <prj> -path <out>} 把用户输入拼成插件侧
 * {@code -action export -path <out>} 的参数并交给 {@link VpCliLauncher}，
 * 透传其退出码。
 */
@Command(
    name = "vp-export",
    mixinStandardHelpOptions = true,
    description = "导出模型到 PlantUML。",
    version = "vpcli 1.1.1"
)
public final class VPExport implements Callable<Integer> {

    @Option(names = "-project", required = true,
            description = "Visual Paradigm 工程文件路径 (.vpp)")
    String project = "";

    @Option(names = "-path", required = true,
            description = "输出目录")
    String path = "";

    @Override
    public Integer call() {
        try {
            String canonicalPath = new File(path).getCanonicalPath();
            return runSafe(() -> VpCliLauncher.launch(PluginArgs.of(
                project, List.of("export", canonicalPath)),project,"-action", "export", "-path", canonicalPath ));
        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new VPExport()).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private static int runSafe(LaunchTask task) {
        try {
            return task.run();
        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
            return 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("中断: " + e.getMessage());
            return 1;
        }
    }

    @FunctionalInterface
    private interface LaunchTask {
        int run() throws IOException, InterruptedException;
    }
}
