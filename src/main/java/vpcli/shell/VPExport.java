package vpcli.shell;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
    name = "vp-export",
    mixinStandardHelpOptions = true,
    description = "export the model to PlantUML files",
    version = "Sky-First-JiaZi 1.1.1"
)
public final class VPExport implements Callable<Integer> {

    @Option(names = "-project", required = true,
            description = "Visual Paradigm project path (*.vpp)")
    String projectPath = "";

    @Option(names = "-output", required = true,
            description = "output path")
    String outputPath = "";

    @Override
    public Integer call() {
        try {
            String canonicalPath = new File(outputPath).getCanonicalPath();
            return runSafe(() -> VpCliLauncher.launch(projectPath, "export","-output", canonicalPath ));
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
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
