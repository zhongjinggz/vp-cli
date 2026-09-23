package plugins.plantUML.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import plugins.plantUML.export.writers.ActivityUMLWriter;
import plugins.plantUML.export.writers.ClassUMLWriter;
import plugins.plantUML.export.writers.ComponentDeploymentUMLWriter;
import plugins.plantUML.export.writers.PlantJSONWriter;
import plugins.plantUML.export.writers.SequenceUMLWriter;
import plugins.plantUML.export.writers.StateUMLWriter;
import plugins.plantUML.export.writers.UseCaseWriter;
import plugins.plantUML.models.SemanticsData;

public class DiagramExportPipelineTest {

    @TempDir
    Path tempDir;

    private DiagramExportPipeline newPipeline() {
        return new DiagramExportPipeline();
    }

    private List<SemanticsData> nonEmptySemantics() {
        List<SemanticsData> list = new ArrayList<>();
        list.add(new SemanticsData());
        return list;
    }

    private IDiagramUIModel diagram(String type, String name) {
        IDiagramUIModel d = mock(IDiagramUIModel.class);
        when(d.getType()).thenReturn(type);
        when(d.getName()).thenReturn(name);
        return d;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MockedConstruction<?> mockedExporter(Class<?> cls, List<SemanticsData> sem) {
        return mockConstruction((Class) cls, (m, c) -> when(((DiagramExporter) m).getExportedSemantics()).thenReturn(sem));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MockedConstruction<?> mockedWriter(Class<?> cls) {
        return mockConstruction((Class) cls);
    }

    private ApplicationManager setUpViewManager(MockedStatic<ApplicationManager> amStatic) {
        ApplicationManager am = mock(ApplicationManager.class);
        ViewManager vm = mock(ViewManager.class);
        when(am.getViewManager()).thenReturn(vm);
        when(vm.getRootFrame()).thenReturn(mock(java.awt.Component.class));
        amStatic.when(ApplicationManager::instance).thenReturn(am);
        return am;
    }

    private void runSupportedCase(String type, String name, Class<?> exp, Class<?> wrt, List<SemanticsData> sem)
        throws Throwable {
        try (MockedConstruction<?> e = mockedExporter(exp, sem);
             MockedConstruction<?> w = mockedWriter(wrt)) {
            captureOut(() -> newPipeline().export(diagram(type, name), tempDir.toFile()));
        }
    }

    @Test
    void export_classDiagram_success() throws Throwable {
        runSupportedCase("ClassDiagram", "C", ClassDiagramExporter.class, ClassUMLWriter.class, null);
        runSupportedCase("ClassDiagram", "C2", ClassDiagramExporter.class, ClassUMLWriter.class,
            Collections.emptyList());
        runSupportedCase("ClassDiagram", "C3", ClassDiagramExporter.class, ClassUMLWriter.class, nonEmptySemantics());
    }

    @Test
    void export_componentDiagram_success() throws Throwable {
        runSupportedCase("ComponentDiagram", "CD", ComponentDeploymentDiagramExporter.class,
            ComponentDeploymentUMLWriter.class, null);
        runSupportedCase("ComponentDiagram", "CD2", ComponentDeploymentDiagramExporter.class,
            ComponentDeploymentUMLWriter.class, Collections.emptyList());
        runSupportedCase("ComponentDiagram", "CD3", ComponentDeploymentDiagramExporter.class,
            ComponentDeploymentUMLWriter.class, nonEmptySemantics());
    }

    @Test
    void export_deploymentDiagram_success() throws Throwable {
        runSupportedCase("DeploymentDiagram", "DD", ComponentDeploymentDiagramExporter.class,
            ComponentDeploymentUMLWriter.class, null);
        runSupportedCase("DeploymentDiagram", "DD2", ComponentDeploymentDiagramExporter.class,
            ComponentDeploymentUMLWriter.class, Collections.emptyList());
        runSupportedCase("DeploymentDiagram", "DD3", ComponentDeploymentDiagramExporter.class,
            ComponentDeploymentUMLWriter.class, nonEmptySemantics());
    }

    @Test
    void export_interactionDiagram_success() throws Throwable {
        runSupportedCase("InteractionDiagram", "S", SequenceDiagramExporter.class, SequenceUMLWriter.class, null);
        runSupportedCase("InteractionDiagram", "S2", SequenceDiagramExporter.class, SequenceUMLWriter.class,
            Collections.emptyList());
        runSupportedCase("InteractionDiagram", "S3", SequenceDiagramExporter.class, SequenceUMLWriter.class,
            nonEmptySemantics());
    }

    @Test
    void export_useCaseDiagram_success() throws Throwable {
        runSupportedCase("UseCaseDiagram", "U", UseCaseDiagramExporter.class, UseCaseWriter.class, null);
        runSupportedCase("UseCaseDiagram", "U2", UseCaseDiagramExporter.class, UseCaseWriter.class,
            Collections.emptyList());
        runSupportedCase("UseCaseDiagram", "U3", UseCaseDiagramExporter.class, UseCaseWriter.class,
            nonEmptySemantics());
    }

    @Test
    void export_stateDiagram_success() throws Throwable {
        runSupportedCase("StateDiagram", "ST", StateDiagramExporter.class, StateUMLWriter.class, null);
        runSupportedCase("StateDiagram", "ST2", StateDiagramExporter.class, StateUMLWriter.class,
            Collections.emptyList());
        runSupportedCase("StateDiagram", "ST3", StateDiagramExporter.class, StateUMLWriter.class, nonEmptySemantics());
    }

    @Test
    void export_activityDiagram_success() throws Throwable {
        runSupportedCase("ActivityDiagram", "A", ActivityDiagramExporter.class, ActivityUMLWriter.class, null);
        runSupportedCase("ActivityDiagram", "A2", ActivityDiagramExporter.class, ActivityUMLWriter.class,
            Collections.emptyList());
        runSupportedCase("ActivityDiagram", "A3", ActivityDiagramExporter.class, ActivityUMLWriter.class,
            nonEmptySemantics());
    }

    @Test
    void export_unsupportedType_throwsUnfit() throws Throwable {
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class)) {
            ApplicationManager am = setUpViewManager(amStatic);
            IDiagramUIModel d = diagram("BogusDiagram", "B");
            assertThrows(UnfitForExportException.class, () -> newPipeline().export(d, tempDir.toFile()));
            verify(am, times(2)).getViewManager();
        }
    }

    @Test
    void export_writerThrowsIOException_printsAndRethrows() throws Throwable {
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<?> e = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w = mockConstruction(ClassUMLWriter.class, (m, c) -> {
                 doThrow(new IOException("boom")).when(m).writeToFile(any(File.class));
             })) {
            ApplicationManager am = setUpViewManager(amStatic);
            IDiagramUIModel d = diagram("ClassDiagram", "IO");
            assertThrows(IOException.class, () -> newPipeline().export(d, tempDir.toFile()));
            verify(am, times(2)).getViewManager();
        }
    }

    @Test
    void exportPartialSemantics_nonEmpty_returnsList() {
        DiagramExporter de = mock(DiagramExporter.class);
        List<SemanticsData> list = nonEmptySemantics();
        when(de.getExportedSemantics()).thenReturn(list);
        assertEquals(list, newPipeline().exportPartialSemantics(de));
    }

    @Test
    void exportPartialSemantics_empty_returnsNull() {
        DiagramExporter de = mock(DiagramExporter.class);
        when(de.getExportedSemantics()).thenReturn(Collections.emptyList());
        assertNull(newPipeline().exportPartialSemantics(de));
    }

    @Test
    void exportPartialSemantics_null_returnsNull() {
        DiagramExporter de = mock(DiagramExporter.class);
        when(de.getExportedSemantics()).thenReturn(null);
        assertNull(newPipeline().exportPartialSemantics(de));
    }

    @Test
    void createOutputFile_jsonType_buildsSemanticsPuml() throws Exception {
        File f = newPipeline().createOutputFile("project_semantics", "json", tempDir.toFile());
        assertEquals("project_semantics_semantics.puml", f.getName());
        assertTrue(f.exists());
    }

    @Test
    void createOutputFile_umlType_sanitizesName() throws Exception {
        File f = newPipeline().createOutputFile("My Diagram!", "uml", tempDir.toFile());
        assertEquals("My_Diagram_.puml", f.getName());
        assertTrue(f.exists());
    }

    @Test
    void createOutputFile_existingFile_isReturnedUnchanged() throws Exception {
        Path target = tempDir.resolve("Existing.puml");
        Files.createFile(target);
        File f = newPipeline().createOutputFile("Existing", "uml", tempDir.toFile());
        assertEquals("Existing.puml", f.getName());
        assertTrue(f.exists());
    }

    @Test
    void createOutputFile_whenCreateFails_throwsIo() throws Exception {
        // Force File.createNewFile() to return false so the defensive dead-branch is taken.
        try (MockedConstruction<File> fc = mockConstruction(File.class, (m, c) -> {
            when(m.exists()).thenReturn(false);
            when(m.createNewFile()).thenReturn(false);
        })) {
            assertThrows(IOException.class, () -> newPipeline().createOutputFile("t", "uml", tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_emptyList_returnsTrue() throws Exception {
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertTrue(newPipeline().exportDiagramList(Collections.emptyList(), null));
        }
    }

    @Test
    void exportDiagramList_allSuccess_returnsTrue() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "L1");
        try (MockedConstruction<?> e = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w = mockedWriter(ClassUMLWriter.class);
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertTrue(newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_unsupportedDiagram_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("WeirdDiagram", "W");
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            setUpViewManager(amStatic);
            assertTrue(!newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_writerIOException_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "IO");
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<?> e = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w = mockConstruction(ClassUMLWriter.class, (m, c) -> {
                 doThrow(new IOException("boom")).when(m).writeToFile(any(File.class));
             });
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            setUpViewManager(amStatic);
            assertTrue(!newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_writerUnsupportedOperation_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "UOE");
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<?> e = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w = mockConstruction(ClassUMLWriter.class, (m, c) -> {
                 doThrow(new UnsupportedOperationException("nope")).when(m).writeToFile(any(File.class));
             });
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            setUpViewManager(amStatic);
            assertTrue(!newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_jsonWriteFails_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "J");
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<?> e = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w = mockedWriter(ClassUMLWriter.class);
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            setUpViewManager(amStatic);
            pj.when(() -> PlantJSONWriter.writeToFile(any(File.class), any(List.class)))
                .thenThrow(new IOException("json boom"));
            assertTrue(!newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportAllDiagrams_exportsEveryDiagram() {
        IDiagramUIModel clazz = diagram("ClassDiagram", "C");
        IDiagramUIModel seq = diagram("InteractionDiagram", "S");
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<?> e1 = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w1 = mockedWriter(ClassUMLWriter.class);
             MockedConstruction<?> e2 = mockedExporter(SequenceDiagramExporter.class, null);
             MockedConstruction<?> w2 = mockedWriter(SequenceUMLWriter.class);
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            IProject project = mock(IProject.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            when(pm.getProject()).thenReturn(project);
            when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[]{clazz, seq});
            newPipeline().exportAllDiagrams(tempDir.toFile());
            verify(pm).getProject();
        }
    }

    @Test
    void exportSpecificDiagram_exportsTarget() throws Exception {
        IDiagramUIModel target = diagram("ClassDiagram", "T");
        try (MockedStatic<ApplicationManager> amStatic = mockStatic(ApplicationManager.class);
             MockedConstruction<?> e = mockedExporter(ClassDiagramExporter.class, null);
             MockedConstruction<?> w = mockedWriter(ClassUMLWriter.class);
             MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            ApplicationManager am = mock(ApplicationManager.class);
            ProjectManager pm = mock(ProjectManager.class);
            IProject project = mock(IProject.class);
            amStatic.when(ApplicationManager::instance).thenReturn(am);
            when(am.getProjectManager()).thenReturn(pm);
            when(pm.getProject()).thenReturn(project);
            when(project.getDiagramById("X")).thenReturn(target);
            newPipeline().exportSpecificDiagram("X", tempDir.toFile());
            verify(project).getDiagramById("X");
        }
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