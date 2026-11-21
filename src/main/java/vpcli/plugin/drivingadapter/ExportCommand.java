package vpcli.plugin.drivingadapter;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import jakarta.inject.Singleton;
import vpcli.plugin.application.ExportService;
import vpcli.plugin.util.BusinessException;
import vpcli.plugin.util.MyIO;

import java.io.File;
import java.util.Objects;

public class ExportCommand {
    private static final org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(ExportCommand.class);

    private final ExportService exportService;
    private final MyIO myIO;
    private final String output;

    @AssistedInject
    public ExportCommand(
        ExportService exportService,
        MyIO myIO,
        @Assisted String[] args
    ) {
        this.exportService = exportService;
        this.myIO = myIO;
        this.output = getOutput(args);
    }

    public void execute() {
//        getOutput(args);
//        FindBugs.requireNonNull(output, "'-output' should not be null. It should be a bug since shell.VPExport should have checked it.");
        performExport(output);
    }

    private String getOutput(String[] args) {
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            if (Objects.equals(arg, "-output")) {
                if (i + 1 < args.length) {
                    return args[++i];
                } else {
                    throw new BusinessException("value of -output is missing!");
                }
            }
        }
        throw new BusinessException("-output is missing!");
    }

    void performExport(String output) {
        File exportLocation = new File(output);

        // Check if the given path exists and is a directory
        // TODO check it in shell
        if (!exportLocation.exists()) {
            if (!exportLocation.mkdirs()) {
                myIO.println("Error: Could not create the specified directory.");
                return;
            }
        }

        // TODO replace myIO.print to BusinessException
        if (!exportLocation.isDirectory()) {
            myIO.println("Error: The specified output path is not a directory.");
            return;
        }

        log.info("Exporting diagram(s) to path: {}", output);

        exportService.export(exportLocation);
    }
}
