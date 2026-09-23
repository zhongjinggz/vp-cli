package plugins.plantUML;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vp.plugin.*;

import com.vp.plugin.diagram.IDiagramUIModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.plantUML.export.DiagramExportPipeline;
import plugins.plantUML.imports.importers.DiagramImportPipeline;

import static plugins.plantUML.PlantUML.CliParams.*;

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

        CliParams params = acquireParams(args);

        if (params.isInvalid()) {
            System.out.println(params.getErrorMessage());
            return;
        }

        // 按 action 分发到导入/导出/列举图表等分支

        switch (params.get("action").toLowerCase()) {
            case "import":
                if (params.isUndefined("path") || params.isNoValue("path")) {
                    System.out.println("Error: Missing required argument -path for import.");
                    return;
                }
                performImport(params.get("path"));
                break;

            case "export":
                // -list 优先：仅列举项目内可用图表而不导出
                if (params.get("list") == "true") {
                    listAvailableDiagrams();
                    return;
                }

                // export 需要同时指定目标图表与输出路径
                if (params.isUndefined("target") || params.isNoValue("target")
                    || params.isUndefined("path") || params.isNoValue("path")) {
                    System.out.println("Error: Missing required arguments for export. Use -target and -path.");
                    return;
                }

                performExport(params.get("target"), params.get("path"));
                break;

            default:
                System.out.println("Error: Invalid action specified. Use 'import' or 'export'.");
        }
    }

    @NonNull
    CliParams acquireParams(String[] args) {
        CliParams cliParams = new CliParams();

        if (args == null || args.length < 2) {
            cliParams.setErrorMessage("Usage: -action <import|export> -path <file_or_folder_path>");
        } else {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-action":
                        if (i + 1 < args.length) {
                            cliParams.set(P_ACTION, args[++i]);
                        } else {
                            cliParams.set(P_ACTION, NO_VALUE);
                            cliParams.setErrorMessage("Error: Missing value for -action");
                            break;
                        }
                        break;

                    case "-path":
                        if (i + 1 < args.length) {
//                            cliParams.set("path", args[++i]);
                            cliParams.set(P_PATH, args[++i]);
                        } else {
                            cliParams.setErrorMessage("Error: Missing value for -path");
                            break;
                        }
                        break;

                    case "-target":
                        if (i + 1 < args.length) {
                            cliParams.set("target", args[++i]);
                        } else {
                            cliParams.setErrorMessage("Error: Missing value for -target");
                            break;
                        }
                        break;

                    case "-list":
                        cliParams.set("list", "true");
                        break;

                    default:
                        cliParams.setErrorMessage("Unknown argument: " + args[i]);
                        break;

                }
            }

            if (cliParams.isUndefined("action")) {
                cliParams.setErrorMessage("Error: Missing required argument -action.");
            }
        }
        return cliParams;
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
        static final String P_ACTION = "action";
        static final String P_TARGET = "target";
        static final String P_LIST = "list";
        static final String P_PATH = "path";

        // 以下两个值用于 key/value 形式的参数的初始值，区别在于：
        // UNDEFINED 代表命令行中根本就没有这个参数，例如 对于“the-command -action abc” ， "-path" 就是 UNDEFINED
        // NO_VALUE 代表命令行中有这个参数但是没有设置值，例如 对于“the-command -action abc -path” ， "-path" 就是 NO_VALUE
        static final String UNDEFINED = "undefined";
        static final String NO_VALUE = "no_value";

        // 对于不以 key/value 形式的有名称的参数，初始值为 FALSE，一旦命令行中出现了，则为 TRUE
        // 例如 对于"the-command -path"，“-list”为 FALSE，对于“the-command -list”，“-list"则为 TRUE
        static final String TRUE = "true";
        static final String FALSE = "false";

        private Map<String, String> params = new HashMap<>();
        private String errorMessage = "";

        CliParams() {
            params.put(P_ACTION, UNDEFINED);
            params.put(P_TARGET, UNDEFINED);
            params.put(P_PATH, UNDEFINED);
            params.put(P_LIST, FALSE);

        }

        void set(String key, String value) {
            this.params.put(key, value);
        }

        String get(String key) {
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

        boolean isUndefined(String key) {
            return params.get(key) == UNDEFINED;
        }

        boolean isNoValue(String key) {
            return params.get(key) == NO_VALUE;
        }
    }
}
