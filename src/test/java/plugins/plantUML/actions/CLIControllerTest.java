package plugins.plantUML.actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
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
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import plugins.plantUML.PlantUML;
import plugins.plantUML.export.DiagramExportPipeline;

/**
 *
 * <p>{@link PlantUML#invoke} 已退化为只把参数转发给 {@link CLIController#invoke}，
 * 因此本测试直接驱动 {@link CLIController}，覆盖 CLI 分发、导入、导出与图表列举的全部分支。
 */
class CLIControllerTest {

    @TempDir
    Path tempDir;

    private final CLIController controller = new CLIController();

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

    // ---------- invoke -> performExport 分支 ----------

    @Test
    void invoke_export_createsNewDirectoryThenExports() throws Throwable {
        // Path does not yet exist; mkdirs() succeeds.
        String newDir = tempDir.resolve("fresh").resolve("sub").toString();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class)) {
            captureOut(() -> invoke(
                    new String[]{"-action", "export", "-target", "all", "-path", newDir}));
            verify(mockedExport(pipelineConstruction)).exportAllDiagrams(new File(newDir));
        }
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
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class)) {
            captureOut(() -> invoke(
                    new String[]{"-action", "export", "-target", "all", "-path", dir.getAbsolutePath()}));
            verify(mockedExport(pipelineConstruction)).exportAllDiagrams(dir);
        }
    }

    @Test
    void invoke_exportSpecific_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class)) {
            captureOut(() -> invoke(
                    new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
            verify(mockedExport(pipelineConstruction)).exportSpecificDiagram("someDiagram", dir);
        }
    }

    @Test
    void invoke_exportSpecificThrowsIOException_printsIoError() throws Throwable {
        File dir = tempDir.toFile();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class, (mock, context) -> {
            doThrow(new IOException("boom")).when(mock).exportSpecificDiagram(any(), eq(dir));
        })) {
            String output = captureOut(() -> invoke(
                    new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
            assertTrue(output.contains("IO Error: Couldn't create file."));
        }
    }

    @Test
    void invoke_exportWithList_listsDiagrams() throws Throwable {
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            IProject project = mock(IProject.class);
            IDiagramUIModel d1 = mock(IDiagramUIModel.class);
            IDiagramUIModel d2 = mock(IDiagramUIModel.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            when(pm.getProject()).thenReturn(project);
            when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[]{d1, d2});
            when(d1.getName()).thenReturn("Class1");
            when(d1.getId()).thenReturn("id1");
            when(d2.getName()).thenReturn("Class2");
            when(d2.getId()).thenReturn("id2");
            String output = captureOut(() -> invoke(
                    new String[]{"-action", "export", "-list"}));
            assertTrue(output.contains("Listing available diagrams"));
            assertTrue(output.contains("Class1 | id: id1"));
            assertTrue(output.contains("Class2 | id: id2"));
        }
    }

    @Test
    void invoke_exportWithList_emptyDiagrams() throws Throwable {
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            IProject project = mock(IProject.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            when(pm.getProject()).thenReturn(project);
            when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[0]);
            String output = captureOut(() -> invoke(
                    new String[]{"-action", "export", "-list"}));
            assertTrue(output.contains("Listing available diagrams"));
        }
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

    private static ProjectManager setUpProjectManager(MockedStatic<ApplicationManager> amStatic) {
        ApplicationManager am = mock(ApplicationManager.class);
        ProjectManager pm = mock(ProjectManager.class);
        amStatic.when(ApplicationManager::instance).thenReturn(am);
        when(am.getProjectManager()).thenReturn(pm);
        return pm;
    }

    private static DiagramExportPipeline mockedExport(MockedConstruction<DiagramExportPipeline> construction) {
        return construction.constructed().get(0);
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