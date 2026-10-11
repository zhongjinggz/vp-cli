package plugins.vpcli.drivingadapter;

import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.drivenadapter.MyConsole;

import java.io.File;
import java.io.IOException;

import static plugins.vpcli.drivingadapter.CLIParams.*;

public class CLIController {
    private static final org.slf4j.Logger logger =
        org.slf4j.LoggerFactory.getLogger(CLIController.class);

    private final ExportService exportService;
    private final TreeService treeService;
    private final ListDiagramsService listDiagramsService;
    private final MyConsole myConsole;

    public CLIController(
        ExportService exportService,
        TreeService treeService,
        ListDiagramsService listDiagramsService,
        MyConsole myConsole) {

        this.exportService = exportService;
        this.treeService = treeService;
        this.listDiagramsService = listDiagramsService;
        this.myConsole = myConsole;
    }

    public void invoke(String[] args) throws IOException {
        CLIParams params = CLIParams.valueOf(args);

        if (params.isInvalid()) {
            myConsole.println(params.errorMessage());
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
                myConsole.println("Error: Could not create the specified directory.");
                return;
            }
        }

        if (!exportLocation.isDirectory()) {
            myConsole.println("Error: The specified path is not a directory.");
            return;
        }

        logger.info("Exporting diagram(s): " + target + " to path: " + path);

        if (target.equalsIgnoreCase(CLIParams.VALUE_ALL)) {
            this.exportService.exportAll(exportLocation);
        } else {
            try {
                exportService.exportSpecificDiagram(target, exportLocation);
            } catch (IOException e) {
                myConsole.println("IO Error: Couldn't create file.");
            }
        }
    }

    void performImport() {
        logger.info("TO Be DEV: Importing functions");
    }

    void listAvailableDiagrams() {
        logger.info("Listing available diagrams in the project:");
        listDiagramsService.listDiagrams();
    }

}