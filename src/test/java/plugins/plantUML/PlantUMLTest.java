package plugins.plantUML;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import java.io.FilenameFilter;
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
import plugins.plantUML.export.DiagramExportPipeline;
import plugins.plantUML.imports.importers.DiagramImportPipeline;

/**
 * 测试 {@link PlantUML} 除"命令行格式与校验"之外的行为：
 * 生命周期、导入、导出、图表列举等，需要借助 Mockito 替换真实插件依赖。
 */
public class PlantUMLTest {

    @TempDir
    Path tempDir;

    private final PlantUML plugin = new PlantUML();

    @Test
    void loaded_doesNothing() {
        plugin.loaded(null);
    }

    @Test
    void unloaded_doesNothing() {
        plugin.unloaded();
    }

    @Test
    void invoke_importFile_callsPipelineAndSaves() throws Throwable {
        File puml = Files.createFile(tempDir.resolve("a.puml")).toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", puml.getAbsolutePath()}));
            assertTrue(output.contains("Importing from path:"));
            verify(mockedImport(pipelineConstruction)).importFromSource(any(File.class));
            verify(pm).saveProject();
        }
    }

    @Test
    void invoke_importSingleTxtFile_callsPipelineAndSaves() throws Throwable {
        // .txt is the first arm of the single-file filter (endsWith(".txt") == true).
        File txt = Files.createFile(tempDir.resolve("single.txt")).toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", txt.getAbsolutePath()}));
            verify(mockedImport(pipelineConstruction)).importFromSource(any(File.class));
            verify(pm).saveProject();
        }
    }

    @Test
    void invoke_importSinglePlantumlFile_callsPipelineAndSaves() throws Throwable {
        // Ends with ".txt" (false), ".puml" (false), then ".plantuml" (true).
        File plantuml = Files.createFile(tempDir.resolve("single.plantuml")).toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", plantuml.getAbsolutePath()}));
            verify(mockedImport(pipelineConstruction)).importFromSource(any(File.class));
            verify(pm).saveProject();
        }
    }

    @Test
    void invoke_importDirectoryWithFiles_callsImportMultiple() throws Throwable {
        // Cover every branch of the filename filter (.txt / .puml / .plantuml / none).
        Files.createFile(tempDir.resolve("a.txt"));
        Files.createFile(tempDir.resolve("b.puml"));
        Files.createFile(tempDir.resolve("c.plantuml"));
        Files.createFile(tempDir.resolve("d.xyz"));
        File dir = tempDir.toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", dir.getAbsolutePath()}));
            verify(mockedImport(pipelineConstruction)).importMultipleFiles(any());
        }
    }

    @Test
    void invoke_importDirectoryEmpty_printsNoFiles() throws Throwable {
        File dir = tempDir.toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", dir.getAbsolutePath()}));
            assertTrue(output.contains("No valid .txt, .puml, or .plantuml files found"));
            verify(mockedImport(pipelineConstruction), org.mockito.Mockito.never()).importMultipleFiles(any());
            verify(pm).saveProject();
        }
    }

    @Test
    void invoke_importDirectoryListFilesNull_printsNoFiles() throws Throwable {
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class);
             MockedConstruction<File> fileConstruction = mockConstruction(File.class, (mock, context) -> {
                 when(mock.isDirectory()).thenReturn(true);
                 when(mock.listFiles(any(FilenameFilter.class))).thenReturn(null);
             })) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", "whatever"}));
            assertTrue(output.contains("No valid .txt, .puml, or .plantuml files found"));
        }
    }

    @Test
    void invoke_importUnsupportedFile_printsError() throws Throwable {
        File file = Files.createFile(tempDir.resolve("c.xyz")).toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", file.getAbsolutePath()}));
            assertTrue(output.contains("Unsupported file type"));
            verify(pm).saveProject();
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
            String output = captureOut(() -> plugin.invoke(
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
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "export", "-list"}));
            assertTrue(output.contains("Listing available diagrams"));
        }
    }

    @Test
    void invoke_exportAll_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class)) {
            captureOut(() -> plugin.invoke(
                    new String[]{"-action", "export", "-target", "all", "-path", dir.getAbsolutePath()}));
            verify(mockedExport(pipelineConstruction)).exportAllDiagrams();
        }
    }

    @Test
    void invoke_exportSpecific_callsPipeline() throws Throwable {
        File dir = tempDir.toFile();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class)) {
            captureOut(() -> plugin.invoke(
                    new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
            verify(mockedExport(pipelineConstruction)).exportSpecificDiagram("someDiagram");
        }
    }

    @Test
    void invoke_exportSpecificThrowsIOException_printsIoError() throws Throwable {
        File dir = tempDir.toFile();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class, (mock, context) -> {
            doThrow(new IOException("boom")).when(mock).exportSpecificDiagram(any());
        })) {
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "export", "-target", "someDiagram", "-path", dir.getAbsolutePath()}));
            assertTrue(output.contains("IO Error: Couldn't create file."));
        }
    }

    @Test
    void invoke_exportPathNotCreatable_printsError() throws Throwable {
        // Create a regular file to act as an obstacle parent so mkdirs() fails.
        File barrier = Files.createFile(tempDir.resolve("barrier")).toFile();
        String obstructed = new File(barrier, "sub" + File.separator + "dir").getAbsolutePath();
        String output = captureOut(() -> plugin.invoke(
                new String[]{"-action", "export", "-target", "all", "-path", obstructed}));
        assertTrue(output.contains("Could not create the specified directory"));
    }

    @Test
    void invoke_exportPathNotDirectory_printsError() throws Throwable {
        // A regular file exists but is not a directory.
        File aFile = Files.createFile(tempDir.resolve("afile.txt")).toFile();
        String output = captureOut(() -> plugin.invoke(
                new String[]{"-action", "export", "-target", "all", "-path", aFile.getAbsolutePath()}));
        assertTrue(output.contains("not a directory"));
    }

    @Test
    void invoke_unknownArgument_printError() throws Throwable {
        File file = Files.createFile(tempDir.resolve("d.puml")).toFile();
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<DiagramImportPipeline> pipelineConstruction = mockConstruction(DiagramImportPipeline.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            String output = captureOut(() -> plugin.invoke(
                    new String[]{"-action", "import", "-path", file.getAbsolutePath(), "-bogus"}));
            assertTrue(output.contains("Unknown argument: -bogus"));
            assertFalse(output.contains("Importing from path:"));
        }
    }

    @Test
    void invoke_export_createsNewDirectoryThenExports() throws Throwable {
        // Path does not yet exist; mkdirs() succeeds (created==true).
        String newDir = tempDir.resolve("fresh").resolve("sub").toString();
        try (MockedConstruction<DiagramExportPipeline> pipelineConstruction = mockConstruction(DiagramExportPipeline.class)) {
            captureOut(() -> plugin.invoke(
                    new String[]{"-action", "export", "-target", "all", "-path", newDir}));
            verify(mockedExport(pipelineConstruction)).exportAllDiagrams();
        }
    }

    private DiagramImportPipeline mockedImport(MockedConstruction<DiagramImportPipeline> construction) {
        return construction.constructed().get(0);
    }

    private DiagramExportPipeline mockedExport(MockedConstruction<DiagramExportPipeline> construction) {
        return construction.constructed().get(0);
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