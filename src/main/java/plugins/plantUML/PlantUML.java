package plugins.plantUML;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vp.plugin.*;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.plantUML.export.DiagramExportPipeline;
import plugins.plantUML.imports.importers.DiagramImportPipeline;

public class PlantUML implements VPPlugin, VPPluginCommandLineSupport {

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
    }

    @Override
    public void unloaded() {
    }

    // CLI 入口：解析参数并分发到导入/导出逻辑
    @Override
    public void invoke(String[] args) {
        String action = null;
        String path = null;
        String target = null;
        boolean listDiagrams = false;

        CliParams cliParams = new CliParams();

        if (args == null || args.length < 2) {
            cliParams.setErrorMessage("Usage: -action <import|export> -path <file_or_folder_path>");
            //System.out.println("Usage: -action <import|export> -path <file_or_folder_path>");
            //return;
        } else {
            // 顺序解析各命令行参数
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-action":
                        if (i + 1 < args.length) {
                            action = args[++i];
                            cliParams.setAction(action);
                        } else {
                            cliParams.setAction("NO_VALUE");
                            cliParams.setErrorMessage("Error: Missing value for -action");
                            break;
                            //System.out.println("Error: Missing value for -action");
                            //return;
                        }
                        break;

                    case "-path":
                        if (i + 1 < args.length) {
                            path = args[++i];
                            cliParams.addParam("path", path);
                        } else {
                            cliParams.setErrorMessage("Error: Missing value for -path");
//                            System.out.println("Error: Missing value for -path");
//                            return;
                            break;
                        }
                        break;

                    case "-target":
                        if (i + 1 < args.length) {
                            target = args[++i];
                            cliParams.addParam("target", target);
                        } else {
                            cliParams.setErrorMessage("Error: Missing value for -target");
//                            System.out.println("Error: Missing value for -target");
//                            return;
                            break;
                        }
                        break;

                    case "-list":
                        cliParams.addParam("list", "true");
                        listDiagrams = true;
                        break;

                    default:
                        cliParams.setErrorMessage("Unknown argument: " + args[i]);
                        //System.out.println("Unknown argument: " + args[i]);
                        break;

                }
            }

            if (cliParams.getAction() == "NOT_SET") {
//                if (action == null) {
                cliParams.setErrorMessage("Error: Missing required argument -action.");
                System.out.println("Error: Missing required argument -action.");
                return;
            }
        }

        if (cliParams.isInvalid()) {
            System.out.println(cliParams.getErrorMessage());
            return;
        }


        // 按 action 分发到导入/导出/列举图表等分支
        // switch (action.toLowerCase()) {
        switch (cliParams.getAction().toLowerCase()) {
            case "import":
//                if (path == null) {
                if (cliParams.getParam("path") == null) {
                    System.out.println("Error: Missing required argument -path for import.");
                    return;
                }
//                performImport(path);
                performImport(cliParams.getParam("path"));
                break;

            case "export":
                // -list 优先：仅列举项目内可用图表而不导出
                if (cliParams.getParam("list") == "true") {
//                    if (listDiagrams) {
                    listAvailableDiagrams();
                    return;
                }

                // export 需要同时指定目标图表与输出路径
                if (cliParams.getParam("target") == null || cliParams.getParam("path") == null) {
//                    if (target == null || path == null) {
                    System.out.println("Error: Missing required arguments for export. Use -target and -path.");
                    return;
                }

                performExport(cliParams.getParam("target"), cliParams.getParam("path"));
//                performExport(target, path);
                break;

            default:
                System.out.println("Error: Invalid action specified. Use 'import' or 'export'.");
        }
    }

    private void performImport(String path) {
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

    private void performExport(String target, String path) {
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

        if (target.equalsIgnoreCase("all")) {
            pipeline.exportAllDiagrams();
        } else {
            try {
                pipeline.exportSpecificDiagram(target);
            } catch (IOException e) {
                System.out.println("IO Error: Couldn't create file.");
            }
        }
    }


    private void listAvailableDiagrams() {
        System.out.println("Listing available diagrams in the project:");
        ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
        IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
        for (IDiagramUIModel diagram : allDiagrams) {
            System.out.println(diagram.getName() + " | id: " + diagram.getId());
        }
    }

    class CliParams {
        private String action = "NOT_SET";
        private Map<String, String> params = new HashMap<>();
        private String errorMessage = "";

        void setAction(String action) {
            this.action = action;
        }

        void addParam(String key, String value) {
            this.params.put(key, value);
        }

        String getAction() {
            return action;
        }

        String getParam(String key) {
            return params.get(key);
        }

        void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        boolean isInvalid() {
            return !errorMessage.isEmpty();
        }
    }
}
