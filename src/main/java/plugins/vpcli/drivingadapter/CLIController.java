package plugins.vpcli.drivingadapter;

import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;

import java.io.File;
import java.io.IOException;

import static plugins.vpcli.drivingadapter.CLIParams.*;

public class CLIController {
    private final ExportService exportService;
    private final TreeService treeService;
    private final ListDiagramsService listDiagramsService;

    public CLIController(ExportService exportService, TreeService treeService, ListDiagramsService listDiagramsService) {
        this.exportService = exportService;
        this.treeService = treeService;
        this.listDiagramsService = listDiagramsService;
    }

    public void invoke(String[] args) {
        CLIParams params = CLIParams.valueOf(args);

        if (params.isInvalid()) {
            System.out.println(params.errorMessage());
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

    void performExport(String target, String path) {
        File exportLocation = new File(path);

        // Check if the given path exists and is a directory
        if (!exportLocation.exists()) {
            boolean created = exportLocation.mkdirs();
            if (!created) {
                System.out.println("Error: Could not create the specified directory.");
                return;
            }
        }

        if (!exportLocation.isDirectory()) {
            System.out.println("Error: The specified path is not a directory.");
            return;
        }

        System.out.println("Exporting diagram(s): " + target + " to path: " + path);

        if (target.equalsIgnoreCase(CLIParams.VALUE_ALL)) {
            this.exportService.exportAll(exportLocation);
        } else {
            try {
                exportService.exportSpecificDiagram(target, exportLocation);
            } catch (IOException e) {
                System.out.println("IO Error: Couldn't create file.");
            }
        }
    }

    void performImport() {
        System.out.println("Importing functions to be developed");
    }

    void listAvailableDiagrams() {
        System.out.println("Listing available diagrams in the project:");
        listDiagramsService.listDiagrams();
    }

}
