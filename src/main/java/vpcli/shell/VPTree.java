package vpcli.shell;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
    name = "vp-tree",
    mixinStandardHelpOptions = true,
    description = "Show the model with tree structure",
    version = "Sky-First-JiaZi 1.1.1"
)
public final class VPTree implements Callable<Integer> {

    @Option(names = "-project", required = true,
            description = "Visual Paradigm project path (*.vpp)")
    String projectPath = "";

    @Override
    public Integer call() {
        return runSafe(() ->
            VpCliLauncher.launch(projectPath,"-action", "tree")
        );
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
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    @FunctionalInterface
    private interface LaunchTask {
        int run() throws IOException, InterruptedException;
    }
}
