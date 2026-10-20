package plugins.vpcli.drivingadapter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
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
import plugins.vpcli.VPCLI;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.util.MyIO;

/**
 *
 * <p>{@link VPCLI#invoke} 已退化为只把参数转发给 {@link CLIController#invoke}，
 * 因此本测试直接驱动 {@link CLIController}，覆盖 CLI 分发、导入、导出与图表列举的全部分支。
 * <p>{@link CLIController} 通过构造器注入 {@link ExportService}，测试用 mock 注入以验证交互；
 * 图表列举等管线内部逻辑（{@code DiagramExportPipeline#listDiagrams}）交由 DiagramExportPipelineTest 覆盖。
 * <p>用户面向的消息输出通过注入的 {@link MyIO} 完成，测试用 mock 验证交互，
 * 不再依赖对 {@code System.out} 的重定向捕获。
 */
class CLIControllerTest {

    @TempDir
    Path tempDir;
    private final MyIO myIOMock = mock(MyIO.class);
    private final ExportService exportServiceMock = mock(ExportService.class);
    private final TreeService treeServiceMock = mock(TreeService.class);
    private final ListDiagramsService listDiagramsServiceMock = mock(ListDiagramsService.class);
    private final CLIController controller = new CLIController(exportServiceMock, treeServiceMock, this.listDiagramsServiceMock,myIOMock);

    private void invoke(String[] args) throws IOException {
        controller.invoke(args);
    }

    // ---------- invoke 的无效参数分支 ----------

    @Test
    void invoke_invalidParams_printsErrorAndReturns() throws Throwable {
        invokeNoThrow(() -> invoke(new String[]{"-path", "x"}));
        verify(myIOMock).println(contains("Missing required argument -action"));
    }

    // ---------- invoke -> performImport 分支 ----------

    @Test
    void invoke_import_callsPerformImport() throws Throwable {
        // performImport 当前为 TBD 占位实现，打印开发提示到日志；仅验证分发执行不抛异常
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "import", "-path", "some.puml"}));
    }

    // ---------- invoke -> performExport 分支 ----------

    @Test
    void invoke_export_createsNewDirectoryThenExports() throws Throwable {
        // Path does not yet exist; mkdirs() succeeds.
        String newDir = tempDir.resolve("fresh").resolve("sub").toString();
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", newDir}));
        verify(exportServiceMock).exportAll(new File(newDir));
    }

    @Test
    void invoke_exportPathNotCreatable_printsError() throws Throwable {
        // A regular file is an obstacle so that mkdirs() returns false.
        File barrier = Files.createFile(tempDir.resolve("barrier")).toFile();
        String obstructed = new File(barrier, "sub" + File.separator + "dir").getAbsolutePath();
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", obstructed}));
        verify(myIOMock).println(contains("Could not create the specified directory"));
    }

    @Test
    void invoke_exportPathNotDirectory_printsError() throws Throwable {
        // Path exists but is a regular file, not a directory.
        File aFile = Files.createFile(tempDir.resolve("afile.txt")).toFile();
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", aFile.getAbsolutePath()}));
        verify(myIOMock).println(contains("not a directory"));
    }

    @Test
    void invoke_exportAll_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "export", "-target", "all", "-path", dir.getAbsolutePath()}));
        verify(exportServiceMock).exportAll(dir);
    }

    @Test
    void invoke_exportSpecific_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
        verify(exportServiceMock).exportSpecificDiagram("someDiagram", dir);
    }


    // ---------- invoke -> listAvailableDiagrams 分支（转调 pipeline 列举） ----------

    @Test
    void invoke_执行列出图表清单命令_则listDiagrams被调用() throws Throwable {
        invokeNoThrow(() -> invoke(
                new String[]{"-action", "list-diagrams"}));
        verify(listDiagramsServiceMock).listDiagrams();
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

    private static void invokeNoThrow(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            throw new AssertionError("invoke expected to complete, but threw: " + t, t);
        }
    }

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
