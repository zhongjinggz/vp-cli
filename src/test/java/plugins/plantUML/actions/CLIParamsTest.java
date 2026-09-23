package plugins.plantUML.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/**
 * 单元测试 {@link CLIParams}，目标是对应文件的 100% 分支覆盖。
 *
 * <p>说明：类内部含有两处仅靠外部 API 无法触达的“防御性”分支：
 * 1) {@code acquireParams} 中 {@code args == null}（构造器总是把 null 归一化为空数组）；
 * 2) {@code set(String,String)} 与 {@code set(String)} 中不属于对应 Map 的 throw 分支。
 * 为达到 100% 分支覆盖，测试通过反射注入私有字段 / 调用私有方法以触达这些分支。
 */
class CLIParamsTest {

    // ---------- 正常/合法参数 ----------

    @Test
    void valueOf_validImport_parsesWithoutError() {
        CLIParams params = CLIParams.valueOf(new String[]{"-action", "import", "-path", "folder"});

        assertFalse(params.isInvalid());
        assertEquals("", params.errorMessage());
        assertEquals("import", params.action().text());
        assertFalse(params.action().isUnset());
        assertFalse(params.action().isNonValue());
        assertEquals("folder", params.path().text());
        assertFalse(params.path().isUnset());
        assertFalse(params.path().isNonValue());
    }

    @Test
    void valueOf_validExport_parsesWithoutError() {
        CLIParams params = CLIParams.valueOf(
                new String[]{"-action", "export", "-target", "all", "-path", "out"});

        assertFalse(params.isInvalid());
        assertEquals("export", params.action().text());
        assertFalse(params.target().isUnset());
        assertFalse(params.target().isNonValue());
        assertFalse(params.path().isUnset());
        assertFalse(params.path().isNonValue());
    }

    @Test
    void valueOf_exportWithList_skipsTargetAndPathValidation() {
        CLIParams params = CLIParams.valueOf(new String[]{"-action", "export", "-list"});

        assertFalse(params.isInvalid());
        assertTrue(params.list().isSet());
        // -list 存在时，即使未提供 -target/-path 也不报错
        assertTrue(params.target().isUnset());
        assertTrue(params.path().isUnset());
    }

    // ---------- action 相关错误分支 ----------

    @Test
    void valueOf_missingAction_reportsError() {
        CLIParams params = CLIParams.valueOf(new String[]{"-path", "x"});

        assertTrue(params.isInvalid());
        assertTrue(params.action().isUnset());
        assertTrue(params.errorMessage().contains("Missing required argument -action"));
    }

    @Test
    void valueOf_invalidAction_reportsError() {
        CLIParams params = CLIParams.valueOf(new String[]{"-action", "bogus", "-path", "x"});

        assertTrue(params.isInvalid());
        assertFalse(params.action().isUnset());
        assertTrue(params.errorMessage().contains("Invalid action specified"));
    }

    @Test
    void valueOf_actionLiteralNonValue_hitsDefaultSwitchBranch() {
        // "non_value" 作为字面量传入 action，落入 switch 的 default 分支
        CLIParams params = CLIParams.valueOf(
                new String[]{"-action", "non_value", "-target", "all", "-path", "x"});

        assertTrue(params.isInvalid());
        assertTrue(params.action().isNonValue());
        assertTrue(params.errorMessage().contains("Invalid action specified"));
    }

    // ---------- import 相关错误分支 ----------

    @Test
    void valueOf_importMissingPath_reportsError() {
        CLIParams params = CLIParams.valueOf(new String[]{"-action", "import"});

        assertTrue(params.isInvalid());
        assertTrue(params.path().isUnset());
        assertTrue(params.errorMessage().contains("-path for import"));
    }

    @Test
    void valueOf_importPathLiteralNonValue_reportsError() {
        // path 未设置真实的非空值，而是字面量 non_value，属于 isNonValue 分支
        CLIParams params = CLIParams.valueOf(
                new String[]{"-action", "import", "-path", "non_value"});

        assertTrue(params.isInvalid());
        assertFalse(params.path().isUnset());
        assertTrue(params.path().isNonValue());
        assertTrue(params.errorMessage().contains("-path for import"));
    }

    // ---------- export 相关错误分支 ----------

    @Test
    void valueOf_exportMissingArguments_reportsError() {
        CLIParams params = CLIParams.valueOf(new String[]{"-action", "export"});

        assertTrue(params.isInvalid());
        assertTrue(params.target().isUnset());
        assertTrue(params.errorMessage().contains("Missing required arguments for export"));
    }

    @Test
    void valueOf_exportTargetLiteralNonValue_reportsError() {
        CLIParams params = CLIParams.valueOf(
                new String[]{"-action", "export", "-target", "non_value", "-path", "out"});

        assertTrue(params.isInvalid());
        assertFalse(params.target().isUnset());
        assertTrue(params.target().isNonValue());
        assertTrue(params.errorMessage().contains("Missing required arguments for export"));
    }

    @Test
    void valueOf_exportPathUnset_reportsError() {
        CLIParams params = CLIParams.valueOf(new String[]{"-action", "export", "-target", "all"});

        assertTrue(params.isInvalid());
        assertFalse(params.target().isUnset());
        assertTrue(params.path().isUnset());
        assertTrue(params.errorMessage().contains("Missing required arguments for export"));
    }

    @Test
    void valueOf_exportPathLiteralNonValue_reportsError() {
        CLIParams params = CLIParams.valueOf(
                new String[]{"-action", "export", "-target", "all", "-path", "non_value"});

        assertTrue(params.isInvalid());
        assertFalse(params.target().isUnset());
        assertFalse(params.path().isUnset());
        assertTrue(params.path().isNonValue());
        assertTrue(params.errorMessage().contains("Missing required arguments for export"));
    }

    // ---------- acquireParams 退出分支（usage / 未知参数 / 缺值） ----------

    @Test
    void constructor_nullArgs_normalizesToEmpty() {
        // 覆盖构造器中三元表达式 args == null 的 null 分支（归一化为空数组 -> Usage）
        CLIParams params = new CLIParams(null);
        params.parse();

        assertTrue(params.isInvalid());
        assertTrue(params.errorMessage().contains("Usage:"));
    }

    @Test
    void parse_emptyArgs_showsUsage() {
        CLIParams params = new CLIParams(new String[0]);
        params.parse();

        assertTrue(params.isInvalid());
        assertTrue(params.errorMessage().contains("Usage:"));
    }

    @Test
    void parse_singleArg_showsUsage() {
        CLIParams params = new CLIParams(new String[]{"x"});
        params.parse();

        assertTrue(params.isInvalid());
        assertTrue(params.errorMessage().contains("Usage:"));
    }

    @Test
    void parse_nullArgs_showsUsage_throughReflection() throws Exception {
        // 字段 args 构造时总是非 null；只有反射注入 null 才能覆盖 args==null 分支
        CLIParams params = new CLIParams(new String[]{"-action", "import"});
        setArgs(params, null);

        params.parse();

        assertTrue(params.isInvalid());
        assertTrue(params.errorMessage().contains("Usage:"));
    }

    @Test
    void parse_unknownArgument_reportsUnknown() {
        CLIParams params = new CLIParams(new String[]{"-action", "import", "-path", "dir", "bogus"});
        params.parse();

        assertTrue(params.isInvalid());
        assertTrue(params.errorMessage().contains("Unknown argument: bogus"));
    }

    @Test
    void parse_trailingKeyValueMissingValue_reportsMissingValue() {
        CLIParams params = new CLIParams(new String[]{"-action", "import", "-path"});
        params.parse();

        assertTrue(params.isInvalid());
        assertTrue(params.errorMessage().contains("Missing value for -path"));
        assertTrue(params.path().isNonValue());
    }

    // ---------- set() 的防御性 throw 分支（反射触达） ----------

    @Test
    void setKeyValue_withUnknownKey_throwsIllegalArgument() throws Exception {
        CLIParams params = new CLIParams(new String[0]);
        Method m = CLIParams.class.getDeclaredMethod("set", String.class, String.class);
        m.setAccessible(true);

        InvocationTargetException ite = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(params, "not-a-key", "value"));
        assertEquals(IllegalArgumentException.class, ite.getCause().getClass());
    }

    @Test
    void setKeyOnly_withUnknownKey_throwsIllegalArgument() throws Exception {
        CLIParams params = new CLIParams(new String[0]);
        Method m = CLIParams.class.getDeclaredMethod("set", String.class);
        m.setAccessible(true);

        InvocationTargetException ite = assertThrows(
                InvocationTargetException.class,
                () -> m.invoke(params, "not-a-key"));
        assertEquals(IllegalArgumentException.class, ite.getCause().getClass());
    }

    // ---------- 辅助工具 ----------

    private static void setArgs(CLIParams params, String[] args) throws Exception {
        Field f = CLIParams.class.getDeclaredField("args");
        f.setAccessible(true);
        f.set(params, args);
    }
}