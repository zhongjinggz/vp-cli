package vpcli.plugin.drivingadapter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.application.ExportService;
import vpcli.plugin.util.BusinessException;
import vpcli.plugin.util.MyIO;

import java.io.File;
import java.util.Objects;

@Singleton
public class ExportCommand {
    private static final org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(ExportCommand.class);

    private final ExportService exportService;
    private final MyIO myIO;
    private String output;

    @Inject
    public ExportCommand(ExportService exportService, MyIO myIO) {
        this.exportService = exportService;
        this.myIO = myIO;
    }

    public void execute(String[] args) {
        setArgs(args);
        performExport(output);
    }

    private void setArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            if (Objects.equals(arg, "-output")) {
                if (i+1 < args.length) {
                    this.output = args[++i];
                } else {
                    throw new BusinessException("value of -output is missing!");
                }
            }
        }
        if (output == null) {
            throw new BusinessException("-output is missing!");
        }
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
