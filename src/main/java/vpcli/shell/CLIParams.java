package vpcli.shell;

import java.util.Arrays;

/**
 * 对应 src/main/python/vp_cli_common.py 中的 {@code CLIParams}。
 *
 * <p>封装命令行参数，区分三类参数：
 * <ul>
 *   <li>key/value 形式（{@code -project/-action/-target/-path}）</li>
 *   <li>仅 key 形式（{@code -list}）</li>
 *   <li>未知参数</li>
 * </ul>
 *
 * <p>对于 key/value 形式的参数：
 * <ul>
 *   <li>UNSET 代表命令行中根本没有这个参数（例如 "the-command -action abc" 中 "-path" 就是 UNSET）；</li>
 *   <li>NON_VALUE 代表命令行中有这个参数但没有设置值（例如 "the-command -action abc -path" 中 "-path" 就是 NON_VALUE）。</li>
 * </ul>
 * <p>对于不带 value 的参数（例如 {@code -list}），只需区分 SET 和 UNSET。
 */
public class CLIParams {
    private static final String VALUE_UNSET = VpCliCommon.VALUE_UNSET;
    private static final String VALUE_SET = VpCliCommon.VALUE_SET;
    private static final String VALUE_NON = VpCliCommon.VALUE_NON;

    // 与 Python 版本不同：这里用类属性直接保存典型的 key/value 参数，
    // -list 由 parse 期间直接置为 VALUE_SET。语义与 Python 的 map 划分一致。
    private static final java.util.Set<String> KEY_VALUE_PARAMS =
        java.util.Set.of(
            VpCliCommon.KEY_PROJECT,
            VpCliCommon.KEY_ACTION,
            VpCliCommon.KEY_TARGET,
            VpCliCommon.KEY_PATH
        );

    private static final java.util.Set<String> KEY_ONLY_PARAMS =
        java.util.Set.of(VpCliCommon.KEY_LIST);

    // 复制入参，避免无意中修改
    private final String[] args;
    private int index;

    private String project = VALUE_UNSET;
    private String action = VALUE_UNSET;
    private String path = VALUE_UNSET;
    private String list = VALUE_UNSET;
    private String target = VpCliCommon.VALUE_ALL; // -target 的默认值是 all

    private String errorMessage = "";

    CLIParams(String[] args) {
        this.args = (args == null ? new String[0] : Arrays.copyOf(args, args.length));
        this.index = 0;
    }

    /** 解析命令行参数；遇到无效输入时记录错误并停止。 */
    CLIParams parse() {
        if (args.length == 0) {
            throw new IllegalStateException("== Bug: no args! ==");
        }

        while (index < args.length) {
            Object[] keyValue = parseValue();
            String key = (String) keyValue[0];
            String value = (String) keyValue[1];
            if (isInvalid()) {
                break;
            }

            // TODO 可以根据 KEY 名称推断属性名，从而更简练
            if (key.equals(VpCliCommon.KEY_PROJECT)) {
                project = value;
            } else if (key.equals(VpCliCommon.KEY_ACTION)) {
                action = value;
            } else if (key.equals(VpCliCommon.KEY_TARGET)) {
                target = value;
            } else if (key.equals(VpCliCommon.KEY_PATH)) {
                path = value;
            } else if (key.equals(VpCliCommon.KEY_LIST)) {
                list = value;
            } else {
                throw new IllegalStateException(
                    "== Bug: unknown argument " + key
                        + " should have been handled in 'parseValue()' ==");
            }
        }

        return this;
    }

    /** 解析当前索引处的参数键及其值，返回 [key, value]。 */
    private Object[] parseValue() {
        String key = args[index];
        String value = VALUE_UNSET;

        if (KEY_VALUE_PARAMS.contains(key)) {
            if (index + 1 < args.length) {
                value = args[index + 1];
                index += 1;
            } else {
                value = VALUE_NON;
                setError("Error: Missing value for " + key);
            }
        } else if (KEY_ONLY_PARAMS.contains(key)) {
            value = VALUE_SET;
        } else {
            setError("Unknown argument: " + key);
        }

        index += 1;
        return new Object[]{key, value};
    }

    private void setError(String message) {
        this.errorMessage = message;
    }

    boolean isInvalid() {
        return errorMessage != null && !errorMessage.isEmpty();
    }

    String getProject() {
        return project;
    }

    String getAction() {
        return action;
    }

    String getPath() {
        return path;
    }

    String getList() {
        return list;
    }

    String getTarget() {
        return target;
    }

    String errorMessage() {
        return errorMessage;
    }

    /** 供 shell 入口设置 action 后调用 {@link VpCliCommon#runVpPlugin(CLIParams)}。 */
    void setAction(String action) {
        this.action = action;
    }
}
