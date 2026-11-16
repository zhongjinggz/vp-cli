package vpcli.plugin.drivingadapter;

import vpcli.plugin.application.ExportService;
import vpcli.plugin.application.TreeService;
import vpcli.plugin.util.MyIO;

import java.io.File;
import java.io.IOException;

import static vpcli.plugin.drivingadapter.CLIParams.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CLIController {
    private static final org.slf4j.Logger logger =
        org.slf4j.LoggerFactory.getLogger(CLIController.class);

    private final ExportService exportService;
    private final TreeService treeService;
    private final MyIO myIO;

    @Inject
    public CLIController(
        ExportService exportService,
        TreeService treeService,
        MyIO myIO
    ) {

        this.exportService = exportService;
        this.treeService = treeService;
        this.myIO = myIO;
    }

    public void invoke(String[] args) throws IOException {
        CLIParams params = CLIParams.valueOf(args);

        if (params.isInvalid()) {
            myIO.println(params.errorMessage());
            return;
        }

        switch (params.action().text()) {
            case VALUE_EXPORT:
                performExport(params.path().text());
                break;
            case VALUE_TREE:
                this.treeService.tree2();
                break;
        }
    }

    void performExport(String path) throws IOException {
        File exportLocation = new File(path);

        // Check if the given path exists and is a directory
        if (!exportLocation.exists()) {
            if (!exportLocation.mkdirs()) {
                myIO.println("Error: Could not create the specified directory.");
                return;
            }
        }

        if (!exportLocation.isDirectory()) {
            myIO.println("Error: The specified path is not a directory.");
            return;
        }

        logger.info("Exporting diagram(s) to path: {}", path);

        this.exportService.exportAll(exportLocation);
    }

}