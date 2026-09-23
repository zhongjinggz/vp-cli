package plugins.plantUML.actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedStatic;
import plugins.plantUML.PlantUML;
import plugins.plantUML.export.DiagramExportPipeline;

/**
 *
 * <p>{@link PlantUML#invoke} 已退化为只把参数转发给 {@link CLIController#invoke}，
 * 因此本测试直接驱动 {@link CLIController}，覆盖 CLI 分发、导入、导出与图表列举的全部分支。
 * <p>{@link CLIController} 通过构造器注入 {@link DiagramExportPipeline}，测试用 mock 注入以验证交互；
 * 图表列举等管线内部逻辑（{@code DiagramExportPipeline#listDiagrams}）交由 DiagramExportPipelineTest 覆盖。
 */
class CLIControllerTest {

    @TempDir
    Path tempDir;

    private final DiagramExportPipeline pipelineMock = mock(DiagramExportPipeline.class);
    private final CLIController controller = new CLIController(pipelineMock);

    private void invoke(String[] args) {
        controller.invoke(args);
    }

    // ---------- invoke 的无效参数分支 ----------

    @Test
    void invoke_invalidParams_printsErrorAndReturns() throws Throwable {
        String output = captureOut(() -> invoke(new String[]{"-path", "x"}));
        assertTrue(output.contains("Missing required argument -action."),
                "unexpected output: [" + output + "]");
    }

    // ---------- invoke -> performImport 分支 ----------

    @Test
    void invoke_import_callsPerformImport() throws Throwable {
        String output = captureOut(() -> invoke(
                new String[]{"-action", "import", "-path", "some.puml"}));
        // performImport 当前为 TBD 空实现，仅验证分发执行不抛异常
        assertTrue(output.isEmpty(), "unexpected output: [" + output + "]");
    }

    // ---------- invoke -> performExport 分支 ----------

    @Test
    void invoke_export_createsNewDirectoryThenExports() throws Throwable {
        // Path does not yet exist; mkdirs() succeeds.
        String newDir = tempDir.resolve("fresh").resolve("sub").toString();
        captureOut(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", newDir}));
        verify(pipelineMock).exportAllDiagrams(new File(newDir));
    }

    @Test
    void invoke_exportPathNotCreatable_printsError() throws Throwable {
        // A regular file is an obstacle so that mkdirs() returns false.
        File barrier = Files.createFile(tempDir.resolve("barrier")).toFile();
        String obstructed = new File(barrier, "sub" + File.separator + "dir").getAbsolutePath();
        String output = captureOut(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", obstructed}));
        assertTrue(output.contains("Could not create the specified directory"));
    }

    @Test
    void invoke_exportPathNotDirectory_printsError() throws Throwable {
        // Path exists but is a regular file, not a directory.
        File aFile = Files.createFile(tempDir.resolve("afile.txt")).toFile();
        String output = captureOut(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", aFile.getAbsolutePath()}));
        assertTrue(output.contains("not a directory"));
    }

    @Test
    void invoke_exportAll_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        captureOut(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", dir.getAbsolutePath()}));
        verify(pipelineMock).exportAllDiagrams(dir);
    }

    @Test
    void invoke_exportSpecific_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        captureOut(() -> invoke(
                new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
        verify(pipelineMock).exportSpecificDiagram("someDiagram", dir);
    }

    @Test
    void invoke_exportSpecificThrowsIOException_printsIoError() throws Throwable {
        File dir = tempDir.toFile();
        doThrow(new IOException("boom")).when(pipelineMock).exportSpecificDiagram(any(), eq(dir));
        String output = captureOut(() -> invoke(
                new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
        assertTrue(output.contains("IO Error: Couldn't create file."));
    }

    // ---------- invoke -> listAvailableDiagrams 分支（转调 pipeline 列举） ----------

    @Test
    void invoke_exportWithList_callsPipelineListDiagrams() throws Throwable {
        String output = captureOut(() -> invoke(
                new String[]{"-action", "export", "-list"}));
        assertTrue(output.contains("Listing available diagrams"));
        // 列举细节已移入 DiagramExportPipeline#listDiagrams，此处仅验证转调
        verify(pipelineMock).listDiagrams();
    }

    // ---------- invoke 的 switch 默认分支（action 既非 import 也非 export） ----------

    @Test
    void invoke_unknownAction_switchDefaultsToNoop() throws Throwable {
        CLIParams.ParamValue bogusAction = new CLIParams.ParamValue("bogus");
        CLIParams mockedParams = mock(CLIParams.class);
        when(mockedParams.isInvalid()).thenReturn(false);
        when(mockedParams.action()).thenReturn(bogusAction);
        try (MockedStatic<CLIParams> paramsStatic = mockStatic(CLIParams.class)) {
            paramsStatic.when(() -> CLIParams.valueOf(any(String[].class)))
                    .thenReturn(mockedParams);
            String output = captureOut(() -> invoke(new String[]{"-action", "bogus"}));
            // switch 落到 default 分支，直接 return，不做任何输出
            assertTrue(output.isEmpty(), "unexpected output: [" + output + "]");
        }
    }

    // ---------- 辅助工具 ----------

    private static String captureOut(Executable executable) throws Throwable {
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