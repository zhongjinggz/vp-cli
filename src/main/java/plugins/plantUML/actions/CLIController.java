package plugins.plantUML.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.plantUML.actions.CLIParams;
import plugins.plantUML.export.DiagramExportPipeline;
import plugins.plantUML.imports.importers.DiagramImportPipeline;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static plugins.plantUML.actions.CLIParams.VALUE_EXPORT;
import static plugins.plantUML.actions.CLIParams.VALUE_IMPORT;

public class CLIController {
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
        DiagramExportPipeline pipeline = new DiagramExportPipeline(exportLocation);

        if (target.equalsIgnoreCase(CLIParams.VALUE_ALL)) {
            pipeline.exportAllDiagrams();
        } else {
            try {
                pipeline.exportSpecificDiagram(target);
            } catch (IOException e) {
                System.out.println("IO Error: Couldn't create file.");
            }
        }
    }

    void performImport(String path) {
        System.out.println("Importing from path: " + path);
        File file = new File(path);
        DiagramImportPipeline pipeline = new DiagramImportPipeline();

        if (file.isDirectory()) {
            File[] files = file.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".txt") ||
                    name.toLowerCase().endsWith(".puml") ||
                    name.toLowerCase().endsWith(".plantuml")
            );

            if (files != null && files.length > 0) {
                List<File> fileList = Arrays.asList(files);
                pipeline.importMultipleFiles(fileList);
            } else {
                System.out.println("Error: No valid .txt, .puml, or .plantuml files found in directory.");
            }
        } else {
            if (file.getName().toLowerCase().endsWith(".txt") ||
                file.getName().toLowerCase().endsWith(".puml") ||
                file.getName().toLowerCase().endsWith(".plantuml")) {
                pipeline.importFromSource(file);
            } else {
                System.out.println("Error: Unsupported file type. Only .txt, .puml, and .plantuml are allowed.");
            }
        }
        ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
        projectManager.saveProject();
    }

    void listAvailableDiagrams() {
        System.out.println("Listing available diagrams in the project:");
        ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
        IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
        for (IDiagramUIModel diagram : allDiagrams) {
            System.out.println(diagram.getName() + " | id: " + diagram.getId());
        }
    }
}
