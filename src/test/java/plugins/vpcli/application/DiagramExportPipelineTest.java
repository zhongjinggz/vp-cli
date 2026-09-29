package plugins.vpcli.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedStatic;
import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.application.exporter.*;
import plugins.vpcli.application.writers.ActivityUMLWriter;
import plugins.vpcli.application.writers.ClassUMLWriter;
import plugins.vpcli.application.writers.ComponentDeploymentUMLWriter;
import plugins.vpcli.application.writers.PlantJSONWriter;
import plugins.vpcli.application.writers.SequenceUMLWriter;
import plugins.vpcli.application.writers.StateUMLWriter;
import plugins.vpcli.application.writers.UseCaseWriter;
import plugins.vpcli.domain.myuml.ucommon.SemanticsData;
import plugins.vpcli.util.UnfitForExportException;

public class DiagramExportPipelineTest {

    @TempDir
    Path tempDir;

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ExporterFactory exporterFactory = mock(ExporterFactory.class);
    private final WriterFactory writerFactory = mock(WriterFactory.class);
    private final FileFactory fileFactory = mock(FileFactory.class);

    @BeforeEach
    void defaultFileFactory() {
        when(fileFactory.createFile(any(File.class), any()))
            .thenAnswer(inv -> new File((File) inv.getArgument(0), inv.getArgument(1)));
    }

    private DiagramExportPipeline newPipeline() {
        return new DiagramExportPipeline(projectRepository, exporterFactory, writerFactory, fileFactory);
    }

    private IProject givenProject() {
        IProject project = mock(IProject.class);
        when(projectRepository.getProject()).thenReturn(project);
        return project;
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

    private ClassDiagramExporter givenClassExporter(List<SemanticsData> sem) {
        ClassDiagramExporter cde = mock(ClassDiagramExporter.class);
        when(cde.getExportedSemantics()).thenReturn(sem);
        when(exporterFactory.createClassDiagramExporter(any())).thenReturn(cde);
        return cde;
    }

    private ClassUMLWriter givenClassWriter(ClassDiagramExporter cde) {
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        return w;
    }

    private void givenSuccessExport(String type, List<SemanticsData> sem) throws IOException {
        switch (type) {
        case "ClassDiagram": {
            givenClassWriter(givenClassExporter(sem));
            break;
        }
        case "ComponentDiagram":
        case "DeploymentDiagram": {
            ComponentDeploymentDiagramExporter comde = mock(ComponentDeploymentDiagramExporter.class);
            when(comde.getExportedSemantics()).thenReturn(sem);
            when(exporterFactory.createComponentDeploymentDiagramExporter(any())).thenReturn(comde);
            when(writerFactory.createComponentDeploymentUMLWriter(comde)).thenReturn(mock(ComponentDeploymentUMLWriter.class));
            break;
        }
        case "InteractionDiagram": {
            SequenceDiagramExporter seqde = mock(SequenceDiagramExporter.class);
            when(seqde.getExportedSemantics()).thenReturn(sem);
            when(exporterFactory.createSequenceDiagramExporter(any())).thenReturn(seqde);
            when(writerFactory.createSequenceUMLWriter(seqde)).thenReturn(mock(SequenceUMLWriter.class));
            break;
        }
        case "UseCaseDiagram": {
            UseCaseDiagramExporter ucde = mock(UseCaseDiagramExporter.class);
            when(ucde.getExportedSemantics()).thenReturn(sem);
            when(exporterFactory.createUseCaseDiagramExporter(any())).thenReturn(ucde);
            when(writerFactory.createUseCaseWriter(ucde)).thenReturn(mock(UseCaseWriter.class));
            break;
        }
        case "StateDiagram": {
            StateDiagramExporter stde = mock(StateDiagramExporter.class);
            when(stde.getExportedSemantics()).thenReturn(sem);
            when(exporterFactory.createStateDiagramExporter(any())).thenReturn(stde);
            when(writerFactory.createStateUMLWriter(stde)).thenReturn(mock(StateUMLWriter.class));
            break;
        }
        case "ActivityDiagram": {
            ActivityDiagramExporter acde = mock(ActivityDiagramExporter.class);
            when(acde.getExportedSemantics()).thenReturn(sem);
            when(exporterFactory.createActivityDiagramExporter(any())).thenReturn(acde);
            when(writerFactory.createActivityUMLWriter(acde)).thenReturn(mock(ActivityUMLWriter.class));
            break;
        }
        default:
            throw new IllegalArgumentException("Unsupported diagram type: " + type);
        }
    }

    private void runSupportedCase(String type, String name, List<SemanticsData> sem) throws Throwable {
        givenSuccessExport(type, sem);
        captureOut(() -> newPipeline().export(diagram(type, name), tempDir.toFile()));
        switch (type) {
        case "ClassDiagram":
            verify(exporterFactory, atLeastOnce()).createClassDiagramExporter(any());
            verify(writerFactory, atLeastOnce()).createClassUMLWriter(any());
            break;
        case "ComponentDiagram":
        case "DeploymentDiagram":
            verify(exporterFactory, atLeastOnce()).createComponentDeploymentDiagramExporter(any());
            verify(writerFactory, atLeastOnce()).createComponentDeploymentUMLWriter(any());
            break;
        case "InteractionDiagram":
            verify(exporterFactory, atLeastOnce()).createSequenceDiagramExporter(any());
            verify(writerFactory, atLeastOnce()).createSequenceUMLWriter(any());
            break;
        case "UseCaseDiagram":
            verify(exporterFactory, atLeastOnce()).createUseCaseDiagramExporter(any());
            verify(writerFactory, atLeastOnce()).createUseCaseWriter(any());
            break;
        case "StateDiagram":
            verify(exporterFactory, atLeastOnce()).createStateDiagramExporter(any());
            verify(writerFactory, atLeastOnce()).createStateUMLWriter(any());
            break;
        case "ActivityDiagram":
            verify(exporterFactory, atLeastOnce()).createActivityDiagramExporter(any());
            verify(writerFactory, atLeastOnce()).createActivityUMLWriter(any());
            break;
        default:
            throw new IllegalArgumentException("Unsupported diagram type: " + type);
        }
    }

    @Test
    void export_classDiagram_success() throws Throwable {
        runSupportedCase("ClassDiagram", "C", null);
        runSupportedCase("ClassDiagram", "C2", Collections.emptyList());
        runSupportedCase("ClassDiagram", "C3", nonEmptySemantics());
    }

    @Test
    void export_componentDiagram_success() throws Throwable {
        runSupportedCase("ComponentDiagram", "CD", null);
        runSupportedCase("ComponentDiagram", "CD2", Collections.emptyList());
        runSupportedCase("ComponentDiagram", "CD3", nonEmptySemantics());
    }

    @Test
    void export_deploymentDiagram_success() throws Throwable {
        runSupportedCase("DeploymentDiagram", "DD", null);
        runSupportedCase("DeploymentDiagram", "DD2", Collections.emptyList());
        runSupportedCase("DeploymentDiagram", "DD3", nonEmptySemantics());
    }

    @Test
    void export_interactionDiagram_success() throws Throwable {
        runSupportedCase("InteractionDiagram", "S", null);
        runSupportedCase("InteractionDiagram", "S2", Collections.emptyList());
        runSupportedCase("InteractionDiagram", "S3", nonEmptySemantics());
    }

    @Test
    void export_useCaseDiagram_success() throws Throwable {
        runSupportedCase("UseCaseDiagram", "U", null);
        runSupportedCase("UseCaseDiagram", "U2", Collections.emptyList());
        runSupportedCase("UseCaseDiagram", "U3", nonEmptySemantics());
    }

    @Test
    void export_stateDiagram_success() throws Throwable {
        runSupportedCase("StateDiagram", "ST", null);
        runSupportedCase("StateDiagram", "ST2", Collections.emptyList());
        runSupportedCase("StateDiagram", "ST3", nonEmptySemantics());
    }

    @Test
    void export_activityDiagram_success() throws Throwable {
        runSupportedCase("ActivityDiagram", "A", null);
        runSupportedCase("ActivityDiagram", "A2", Collections.emptyList());
        runSupportedCase("ActivityDiagram", "A3", nonEmptySemantics());
    }

    @Test
    void export_unsupportedType_throwsUnfit() {
        IDiagramUIModel d = diagram("BogusDiagram", "B");
        assertThrows(UnfitForExportException.class, () -> newPipeline().export(d, tempDir.toFile()));
    }

    @Test
    void export_writerThrowsIOException_printsAndRethrows() throws Throwable {
        ClassDiagramExporter cde = givenClassExporter(null);
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        doThrow(new IOException("boom")).when(w).writeToFile(any(File.class));
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        IDiagramUIModel d = diagram("ClassDiagram", "IO");
        assertThrows(IOException.class, () -> newPipeline().export(d, tempDir.toFile()));
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
        File fakeFile = mock(File.class);
        when(fakeFile.exists()).thenReturn(false);
        when(fakeFile.createNewFile()).thenReturn(false);
        when(fileFactory.createFile(any(File.class), any())).thenReturn(fakeFile);
        assertThrows(IOException.class, () -> newPipeline().createOutputFile("t", "uml", tempDir.toFile()));
    }

    @Test
    void exportDiagramList_emptyList_returnsTrue() {
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertTrue(newPipeline().exportDiagramList(Collections.emptyList(), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_allSuccess_returnsTrue() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "L1");
        givenClassWriter(givenClassExporter(null));
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertTrue(newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_unsupportedDiagram_returnsFalse() {
        IDiagramUIModel d = diagram("WeirdDiagram", "W");
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertFalse(newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_writerIOException_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "IO");
        ClassDiagramExporter cde = givenClassExporter(null);
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        doThrow(new IOException("boom")).when(w).writeToFile(any(File.class));
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertFalse(newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_writerUnsupportedOperation_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "UOE");
        ClassDiagramExporter cde = givenClassExporter(null);
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        doThrow(new UnsupportedOperationException("nope")).when(w).writeToFile(any(File.class));
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            assertFalse(newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportDiagramList_jsonWriteFails_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "J");
        givenClassWriter(givenClassExporter(null));
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            pj.when(() -> PlantJSONWriter.writeToFile(any(File.class), any(List.class)))
                .thenThrow(new IOException("json boom"));
            assertFalse(newPipeline().exportDiagramList(Collections.singletonList(d), tempDir.toFile()));
        }
    }

    @Test
    void exportAllDiagrams_exportsEveryDiagram() throws Exception {
        IDiagramUIModel clazz = diagram("ClassDiagram", "C");
        IDiagramUIModel seq = diagram("InteractionDiagram", "S");
        givenClassWriter(givenClassExporter(null));
        SequenceDiagramExporter seqde = mock(SequenceDiagramExporter.class);
        when(seqde.getExportedSemantics()).thenReturn(null);
        when(exporterFactory.createSequenceDiagramExporter(any())).thenReturn(seqde);
        when(writerFactory.createSequenceUMLWriter(seqde)).thenReturn(mock(SequenceUMLWriter.class));
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            IProject project = givenProject();
            when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[]{clazz, seq});
            newPipeline().exportAllDiagrams(tempDir.toFile());
            verify(projectRepository).getProject();
        }
    }

    @Test
    void exportSpecificDiagram_exportsTarget() throws Exception {
        IDiagramUIModel target = diagram("ClassDiagram", "T");
        givenClassWriter(givenClassExporter(null));
        try (MockedStatic<PlantJSONWriter> pj = mockStatic(PlantJSONWriter.class)) {
            IProject project = givenProject();
            when(project.getDiagramById("X")).thenReturn(target);
            newPipeline().exportSpecificDiagram("X", tempDir.toFile());
            verify(project).getDiagramById("X");
        }
    }

    @Test
    void listDiagrams_printsEachDiagram() throws Throwable {
        IDiagramUIModel d1 = diagram("ClassDiagram", "Class1");
        IDiagramUIModel d2 = diagram("InteractionDiagram", "Seq1");
        when(d1.getId()).thenReturn("id1");
        when(d2.getId()).thenReturn("id2");
        IProject project = givenProject();
        when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[]{d1, d2});
        String output = captureOut(() -> newPipeline().listDiagrams());
        assertTrue(output.contains("Class1 | id: id1"));
        assertTrue(output.contains("Seq1 | id: id2"));
        verify(project).toDiagramArray();
    }

    @Test
    void listDiagrams_emptyProject_printsNothing() throws Throwable {
        IProject project = givenProject();
        when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[0]);
        String output = captureOut(() -> newPipeline().listDiagrams());
        assertTrue(output.isEmpty());
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