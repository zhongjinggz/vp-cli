package vpcli.shell;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 独立树形输出命令。
 *
 * <p>{@code vp-tree -project <prj>} 把用户输入拼成插件侧
 * {@code -action tree} 的参数并交给 {@link VpCliLauncher}，透传其退出码。
 */
@Command(
    name = "vp-tree",
    mixinStandardHelpOptions = true,
    description = "Show the model with tree structure",
    version = "Sky-First-JiaZi 1.1.1"
)
public final class VPTree implements Callable<Integer> {

    @Option(names = "-project", required = true,
            description = "Visual Paradigm 工程文件路径 (.vpp)")
    String project = "";

    @Override
    public Integer call() {
        return runSafe(() -> VpCliLauncher.launch(PluginArgs.of(project, List.of("tree")),project,"-action", "tree"));
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new VPTree()).execute(args);
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
