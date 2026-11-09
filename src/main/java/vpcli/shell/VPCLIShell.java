package vpcli.shell;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

// TODO refactor this class later
/**
 * Picocli 命令行入口。
 *
 * <p>同一程序依据第一个位置参数（子命令名）分派到不同功能：
 * <ul>
 *   <li>{@code export}        导出模型到 PlantUML</li>
 *   <li>{@code tree}          以树形输出模型结构</li>
 *   <li>{@code list-diagrams} 列出工程中可用的图表</li>
 * </ul>
 *
 * <p>各子命令负责把用户输入拼成兼容旧插件侧 {@code -action/-path/-target}
 * 的参数（经 {@link PluginArgs} 分层），并交给 {@link VpCliLauncher} 启动
 * VP 插件进程、透传其退出码。
 */
@Command(
    name = "vpcli",
    synopsisSubcommandLabel = "COMMAND",
    subcommands = {
        VPCLIShell.ExportCommand.class,
        VPCLIShell.TreeCommand.class,
        VPCLIShell.ListDiagramsCommand.class
    },
    mixinStandardHelpOptions = true,
    version = "vpcli 1.1.1"
)
public final class VPCLIShell implements Runnable {

    private VPCLIShell() {
    }

    @Override
    public void run() {
        // 未提供子命令时打印 usage。
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new VPCLIShell()).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    // Callable.call() 不能声明 checked 异常，故在此捕获并映射为退出码。
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

    /**
     * {@code vpcli export -project <prj> [-target <n>] -path <out>}
     * <p>缺省 {@code -target=all}（对齐旧插件宽松语义）。对应插件参数
     * {@code -action export -target <n> -path <out>}。
     */
    @Command(
        name = "export",
        mixinStandardHelpOptions = true,
        description = "导出模型到 PlantUML。"
    )
    static final class ExportCommand implements Callable<Integer> {
        @Option(names = "-project", required = true,
                description = "Visual Paradigm 工程文件路径 (.vpp)")
        String project;

        @Option(names = "-target", defaultValue = "all",
                description = "要导出的图表名；缺省导出全部 (all)")
        String target;

        @Option(names = "-path", required = true,
                description = "输出目录")
        String path;

        @Override
        public Integer call() throws IOException {
            String canonicalPath = new File(path).getCanonicalPath();
            return runSafe(() -> VpCliLauncher.launch(PluginArgs.of(
                project, List.of("export", target, canonicalPath))));
        }
    }

    /**
     * {@code vpcli tree -project <prj>}
     * <p>对应插件参数 {@code -action tree}。
     */
    @Command(
        name = "tree",
        mixinStandardHelpOptions = true,
        description = "以树形输出模型结构。"
    )
    static final class TreeCommand implements Callable<Integer> {
        @Option(names = "-project", required = true,
                description = "Visual Paradigm 工程文件路径 (.vpp)")
        String project;

        @Override
        public Integer call() {
            return runSafe(() -> VpCliLauncher.launch(PluginArgs.of(project, List.of("tree"))));
        }
    }

    /**
     * {@code vpcli list-diagrams -project <prj>}
     * <p>对应插件参数 {@code -action list-diagrams}。
     */
    @Command(
        name = "list-diagrams",
        mixinStandardHelpOptions = true,
        description = "列出工程中可用的图表。"
    )
    static final class ListDiagramsCommand implements Callable<Integer> {
        @Option(names = "-project", required = true,
                description = "Visual Paradigm 工程文件路径 (.vpp)")
        String project;

        @Override
        public Integer call() {
            return runSafe(() -> VpCliLauncher.launch(
                PluginArgs.of(project, List.of("list-diagrams"))));
        }
    }
}
