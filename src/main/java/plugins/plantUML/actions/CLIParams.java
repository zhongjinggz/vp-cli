package plugins.plantUML.actions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class CLIParams {
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

    private final String[] args;

    //将参数放在两个Map中，就可以区分哪些参数是 key/value 形式的，哪些不具有 value
    private final Map<String, String> keyValueParams = new HashMap<>();
    private final Map<String, String> keyOnlyParams = new HashMap<>();

    private String errorMessageText = "";

    CLIParams(String[] args) {
        // 复制入参，避免无意中修改
        this.args = (args == null ? new String[0] : Arrays.copyOf(args, args.length));

        keyValueParams.put(KEY_ACTION, VALUE_UNSET);
        keyValueParams.put(KEY_TARGET, VALUE_UNSET);
        keyValueParams.put(KEY_PATH, VALUE_UNSET);

        keyOnlyParams.put(KEY_LIST, VALUE_UNSET);
    }

    // 封装命令行参数
    @NonNull
    static CLIParams valueOf(String[] args) {
        CLIParams params = new CLIParams(args);

        params.parse();
        return params;
    }

    void parse() {
        if (acquireParams()) {
            validateParams();
        }
    }

    private void validateParams() {
        if (action().isUnset()) {
            setErrorMessage("Error: Missing required argument -action.");
        } else {
            switch (action().text()) {
                case VALUE_IMPORT:
                    if (path().isUnset() || path().isNonValue()) {
                        setErrorMessage("Error: Missing required argument -path for import.");
                    }
                    break;
                case VALUE_EXPORT:
                    if (list().isUnset()) {
                        // export 需要同时指定目标图表与输出路径
                        if (target().isUnset() || target().isNonValue()
                            || path().isUnset() || path().isNonValue()) {
                            setErrorMessage("Error: Missing required arguments for export. Use -target and -path.");
                        }
                    }
                    break;
                default:
                    setErrorMessage("Error: Invalid action specified. Use 'import' or 'export'.");
            }
        }


    }

    private boolean acquireParams() {
        if (args == null || args.length < 2) {
            setErrorMessage("Usage: -action <import|export> -path <file_or_folder_path>");
            return false;
        }

        for (int index = 0; index < args.length; index++) {
            String key = args[index];

            if (isKeyValueParam(key)) {
                index = parseValue(index);
            } else if (isKeyOnlyParam(key)) {
                set(key);
            } else {
                setErrorMessage("Unknown argument: " + key);
            }

            if (isInvalid()) {
                return false;
            }
        }

        return true;
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
