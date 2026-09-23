package plugins.plantUML.actions;

import plugins.plantUML.export.DiagramExportPipeline;

import java.io.File;
import java.io.IOException;

import static plugins.plantUML.actions.CLIParams.VALUE_EXPORT;
import static plugins.plantUML.actions.CLIParams.VALUE_IMPORT;

public class CLIController {
    private final DiagramExportPipeline pipeline;
    public CLIController(DiagramExportPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void invoke(String[] args) {
        CLIParams params = CLIParams.valueOf(args);

        if (params.isInvalid()) {
            System.out.println(params.errorMessage());
            return;
        }

        switch (params.action().text()) {
            case VALUE_IMPORT:
                performImport(params.path().text());
                break;
            case VALUE_EXPORT:
                // -list 优先：仅列举项目内可用图表而不导出
                if (params.list().isSet()) {
                    listAvailableDiagrams();
                } else {
                    performExport(params.target().text(), params.path().text());
                }
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
            this.pipeline.exportAllDiagrams(exportLocation);
        } else {
            try {
                pipeline.exportSpecificDiagram(target, exportLocation);
            } catch (IOException e) {
                System.out.println("IO Error: Couldn't create file.");
            }
        }
    }

    void performImport(String path) {
       //TBD
    }

    void listAvailableDiagrams() {
        System.out.println("Listing available diagrams in the project:");
        pipeline.listDiagrams();
    }

}
