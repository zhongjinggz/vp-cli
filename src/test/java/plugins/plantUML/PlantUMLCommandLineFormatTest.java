package plugins.plantUML;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * 专项测试 {@link PlantUML#invoke(String[])} 的命令行参数格式与校验分支。
 * 只关心参数解析、缺参、未知参数与 Usage 提示信息，不触达真实导入/导出逻辑。
 */
public class PlantUMLCommandLineFormatTest {

    private final PlantUML plugin = new PlantUML();

    @Test
    void invoke_nullArgs_showsUsage() throws Throwable {
        String output = captureOut(() -> plugin.invoke(null));
        assertTrue(output.contains("Usage:"));
    }

    @Test
    void invoke_emptyArgs_showsUsage() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[0]));
        assertTrue(output.contains("Usage:"));
    }

    @Test
    void invoke_singleNonFlagArg_showsUsage() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[]{"x"}));
        assertTrue(output.contains("Usage:"));
    }

    @Test
    void invoke_actionMissingValue_returnsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[]{"-path", "y", "-action"}));
        assertTrue(output.contains("Missing value for -action"));
    }

    @Test
    void invoke_pathMissingValue_returnsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[]{"-action", "import", "-path"}));
        assertTrue(output.contains("Missing value for -path"));
    }

    @Test
    void invoke_targetMissingValue_returnsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[]{"-action", "export", "-target"}));
        assertTrue(output.contains("Missing value for -target"));
    }

    @Test
    void invoke_noAction_returnsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[]{"-path", "x"}));
        assertTrue(output.contains("Missing required argument -action"));
    }

    @Test
    void invoke_importMissingPath_returnsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(new String[]{"-action", "import"}));
        assertTrue(output.contains("Missing required argument -path for import"));
    }

    @Test
    void invoke_exportMissingArgs_returnsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(
                new String[]{"-action", "export"}));
        assertTrue(output.contains("Missing required arguments for export"));
    }

    @Test
    void invoke_exportTargetSetButNoPath_printsError() throws Throwable {
        // target != null but path == null => second half of `target == null || path == null`.
        String output = captureOut(() -> plugin.invoke(
                new String[]{"-action", "export", "-target", "all"}));
        assertTrue(output.contains("Missing required arguments for export"));
    }

    @Test
    void invoke_unknownAction_printsError() throws Throwable {
        String output = captureOut(() -> plugin.invoke(
                new String[]{"-action", "bogus", "-path", "x"}));
        assertTrue(output.contains("Invalid action specified"));
    }

    private String captureOut(Executable executable) throws Throwable {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream ps = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(ps);
            executable.execute();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString();
    }
}