package plugins.vpcli.drivingadapter;

import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.util.MyIO;

import java.io.File;
import java.io.IOException;

import static plugins.vpcli.drivingadapter.CLIParams.*;

import jakarta.inject.Singleton;

@Singleton
public class CLIController {
    private static final org.slf4j.Logger logger =
        org.slf4j.LoggerFactory.getLogger(CLIController.class);

    private final ExportService exportService;
    private final TreeService treeService;
    private final ListDiagramsService listDiagramsService;
    private final MyIO myIO;

    public CLIController(
        ExportService exportService,
        TreeService treeService,
        ListDiagramsService listDiagramsService,
        MyIO myIO
    ) {

        this.exportService = exportService;
        this.treeService = treeService;
        this.listDiagramsService = listDiagramsService;
        this.myIO = myIO;
    }

    public void invoke(String[] args) throws IOException {
        CLIParams params = CLIParams.valueOf(args);

        if (params.isInvalid()) {
            myIO.println(params.errorMessage());
            return;
        }

        switch (params.action().text()) {
            case VALUE_IMPORT:
                performImport();
                break;
            case VALUE_EXPORT:
                performExport(params.target().text(), params.path().text());
                break;
            case VALUE_TREE:
                this.treeService.tree();
                break;
            case VALUE_LIST_DIAGRAMS:
                this.listAvailableDiagrams();
                break;
        }
    }

    void performExport(String target, String path) throws IOException {
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

        logger.info("Exporting diagram(s): {} to path: {}", target, path);

        if (target.equalsIgnoreCase(CLIParams.VALUE_ALL)) {
            this.exportService.exportAll(exportLocation);
        } else {
            exportService.exportSpecificDiagram(target, exportLocation);
        }
    }

    void performImport() {
        CLIController.logger.info("TO Be DEV: Importing functions");
    }

    void listAvailableDiagrams() {
        logger.info("Listing available diagrams in the project:");
        listDiagramsService.listDiagrams();
    }

}