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

        CliParams params = CliParams.valueOf(args);

        if (params.isInvalid()) {
            System.out.println(params.errorMessage());
            return;
        }

        switch (params.action().text()) {
//            switch (params.get(KEY_ACTION)) {
            case VALUE_IMPORT:
                if (params.path().isUnset() || params.path().isNonValue()) {
//                    if (params.isUndefined(KEY_PATH) || params.isNonValue(KEY_PATH)) {
                    System.out.println("Error: Missing required argument -path for import.");
                    return;
                }
                performImport(params.path().text());
                break;

            case VALUE_EXPORT:
                // -list 优先：仅列举项目内可用图表而不导出
                if (params.list().isSet()) {
//                    if (params.isTrue(KEY_LIST) ) {
                    listAvailableDiagrams();
                    return;
                }

                // export 需要同时指定目标图表与输出路径
                if (params.target().isUnset() || params.target().isNonValue()
                    || params.path().isUnset() || params.path().isNonValue()) {
//                    if (params.isUndefined(KEY_TARGET) || params.isNonValue(KEY_TARGET)
//                        || params.isUndefined(KEY_PATH) || params.isNonValue(KEY_PATH)) {
                    System.out.println("Error: Missing required arguments for export. Use -target and -path.");
                    return;
                }

                performExport(params.target().text(), params.path().text());
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

        if (target.equalsIgnoreCase(CliParams.VALUE_ALL)) {
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

    // TODO 将其他命令行相关参数移入参数类
    static class CliParams {
        static final String KEY_ACTION = "-action";
        static final String VALUE_IMPORT = "import";
        static final String VALUE_EXPORT = "export";

        static final String KEY_TARGET = "-target";
        static final String VALUE_ALL = "all";

        static final String KEY_PATH = "-path";
        static final String KEY_LIST = "-list";

        // 对于 key/value 形式的参数:
        // UNSET 代表命令行中根本就没有这个参数，例如 对于“the-command -action abc” ， "-path" 就是 UNDEFINED
        // VALUE_NON 代表命令行中有这个参数但是没有设置值，例如 对于“the-command -action abc -path” ， "-path" 就是 NO_VALUE
        // 对于不带 value 的参数，例如 -list，只需要区分 SET 和 UNSET
        static final String VALUE_UNSET = "unset";
        static final String VALUE_SET = "set";
        static final String VALUE_NON = "non_value";

        private String[] args;

        //将参数放在两个Map中，就可以区分哪些参数是 key/value 形式的，哪些不具有 value
        private Map<String, String> keyValueParams = new HashMap<>();
        private Map<String, String> keyOnlyParams = new HashMap<>();


        private String actionText = VALUE_UNSET;
        private String targetText = VALUE_UNSET;
        private String pathText = VALUE_UNSET;
        private String listText = VALUE_UNSET;

        private String errorMessageText = "";

        CliParams(String[] args) {
            this.args = (args == null ? new String[0] : Arrays.copyOf(args, args.length));

            keyValueParams.put(KEY_ACTION, VALUE_UNSET);
            keyValueParams.put(KEY_TARGET, VALUE_UNSET);
            keyValueParams.put(KEY_PATH, VALUE_UNSET);

            keyOnlyParams.put(KEY_LIST, VALUE_UNSET);
        }

        // 封装命令行参数
        @NonNull
        static CliParams valueOf(String[] args) {
            CliParams params = new CliParams(args);

            params.parse();
            return params;
        }

        void parse() {
            if (args == null || args.length < 2) {
                setErrorMessage("Usage: -action <import|export> -path <file_or_folder_path>");
            } else {
                for (int index = 0; index < args.length; index++) {
                    if (isKeyValueParam(args[index])) {
                        index = parseValue(index);
                    } else if (isKeyOnlyParam(args[index])){
                        set(args[index]);
                    } else {
                        setErrorMessage("Unknown argument: " + args[index]);
                    }

                    if (isInvalid()) {
                        break;
                    }
                }

                if (action().isUnset()) {
                    setErrorMessage("Error: Missing required argument -action.");
                }
            }
        }

        private boolean isKeyValueParam(String arg) {
            return keyValueParams.containsKey(arg);
        }

        private boolean isKeyOnlyParam(String arg) {
            return keyOnlyParams.containsKey(arg);
        }

        private int parseValue(int index) {
            String paramKey = args[index];

            if (index + 1 < args.length) {
                String arg = args[++index];
                set(paramKey, arg);
            } else {
                set(paramKey, VALUE_NON);
                setErrorMessage("Error: Missing value for " + paramKey);
            }
            return index;
        }

        // 对于 key/value 形式的参数，赋 value 值
        private void set(String key, String value) {
            if (keyValueParams.containsKey(key)) {
                keyValueParams.put(key, value);
            } else {
                throw new IllegalArgumentException("[Coding Bug] " + key + " is not a key/value argument.");
            }
        }

        // 对于不需要 value 的参数，赋为 “VALUE_SET”
        private void set(String key) {
            if (keyOnlyParams.containsKey(key)) {
                keyOnlyParams.put(key, VALUE_SET);
            } else {
                throw new IllegalArgumentException("[Coding Bug] " + key + " is not a key only argument.");
            }
        }

        void setErrorMessage(String errorMessage) {
            this.errorMessageText = errorMessage;
        }

        String errorMessage() {
            return errorMessageText;
        }

        boolean isInvalid() {
            return !errorMessageText.isEmpty();
        }

        ParamValue action() {
            return new ParamValue(keyValueParams.get(KEY_ACTION));
        }

        ParamValue path() {
            return new ParamValue(keyValueParams.get(KEY_PATH));
        }

        public ParamValue list() {
            return new ParamValue(keyOnlyParams.get(KEY_LIST));
        }

        public ParamValue target() {
            return new ParamValue(keyValueParams.get(KEY_TARGET));
        }
    }

    // 这个类的目的是为了构建流畅的 DSL
    static class ParamValue {
        String text;

        ParamValue(String text) {
            this.text = text;
        }

        boolean isUnset() {
            return text.equals(VALUE_UNSET);
        }

        boolean isNonValue() {
            return text.equals(VALUE_NON);
        }

        boolean isSet() {
            return text.equals(VALUE_SET);
        }

        String text() {
            return this.text;
        }
    }
}
