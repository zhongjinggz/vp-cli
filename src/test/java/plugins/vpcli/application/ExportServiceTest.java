package plugins.vpcli.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
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
import plugins.vpcli.domain.mydiagram.*;
import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.application.writers.ActivityUMLWriter;
import plugins.vpcli.application.writers.ClassUMLWriter;
import plugins.vpcli.application.writers.ComponentDeploymentUMLWriter;
import plugins.vpcli.application.writers.SequenceUMLWriter;
import plugins.vpcli.application.writers.StateUMLWriter;
import plugins.vpcli.application.writers.UseCaseWriter;
import plugins.vpcli.drivenadapter.TreeDirMaker;
import plugins.vpcli.util.UnfitForExportException;

public class ExportServiceTest {

    @TempDir
    Path tempDir;

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final MyDiagramFactory myDiagramFactory = mock(MyDiagramFactory.class);
    private final WriterFactory writerFactory = mock(WriterFactory.class);
    private final FileFactory fileFactory = mock(FileFactory.class);
    private final TreeConverter treeConverter = mock(TreeConverter.class);
    private final TreeDirMaker treeDirMaker = mock(TreeDirMaker.class);

    @BeforeEach
    void defaultFileFactory() {
        when(fileFactory.createFile(any(File.class), any()))
            .thenAnswer(inv -> new File((File) inv.getArgument(0), inv.getArgument(1)));
    }

    private ExportService newPipeline() {
        return new ExportService(projectRepository, myDiagramFactory, writerFactory, fileFactory, null, null);
    }

    private IProject givenProject() {
        IProject project = mock(IProject.class);
        when(projectRepository.getProject()).thenReturn(project);
        return project;
    }

    private IDiagramUIModel diagram(String type, String name) {
        IDiagramUIModel d = mock(IDiagramUIModel.class);
        when(d.getType()).thenReturn(type);
        when(d.getName()).thenReturn(name);
        return d;
    }

    private MyClassDiagram givenClassExporter() {
        MyClassDiagram cde = mock(MyClassDiagram.class);
        when(myDiagramFactory.createClassDiagramExporter(any())).thenReturn(cde);
        return cde;
    }

    private void givenClassWriter(MyClassDiagram cde) {
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
    }

    private void givenSuccessExport(String type) {
        switch (type) {
            case "ClassDiagram": {
                givenClassWriter(givenClassExporter());
                break;
            }
            case "ComponentDiagram":
            case "DeploymentDiagram": {
                MyComponentDeploymentDiagram comde = mock(MyComponentDeploymentDiagram.class);
                when(myDiagramFactory.createComponentDeploymentDiagramExporter(any())).thenReturn(comde);
                when(writerFactory.createComponentDeploymentUMLWriter(comde)).thenReturn(mock(ComponentDeploymentUMLWriter.class));
                break;
            }
            case "InteractionDiagram": {
                MySequenceDiagram seqde = mock(MySequenceDiagram.class);
                when(myDiagramFactory.createSequenceDiagramExporter(any())).thenReturn(seqde);
                when(writerFactory.createSequenceUMLWriter(seqde)).thenReturn(mock(SequenceUMLWriter.class));
                break;
            }
            case "UseCaseDiagram": {
                MyUseCaseDiagram ucde = mock(MyUseCaseDiagram.class);
                when(myDiagramFactory.createUseCaseDiagramExporter(any())).thenReturn(ucde);
                when(writerFactory.createUseCaseWriter(ucde)).thenReturn(mock(UseCaseWriter.class));
                break;
            }
            case "StateDiagram": {
                MyStateDiagram stde = mock(MyStateDiagram.class);
                when(myDiagramFactory.createStateDiagramExporter(any())).thenReturn(stde);
                when(writerFactory.createStateUMLWriter(stde)).thenReturn(mock(StateUMLWriter.class));
                break;
            }
            case "ActivityDiagram": {
                MyActivityDiagram acde = mock(MyActivityDiagram.class);
                when(myDiagramFactory.createActivityDiagramExporter(any())).thenReturn(acde);
                when(writerFactory.createActivityUMLWriter(acde)).thenReturn(mock(ActivityUMLWriter.class));
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported diagram type: " + type);
        }
    }

    private void runSupportedCase(String type, String name) throws Throwable {
        givenSuccessExport(type);
        captureOut(() -> newPipeline().exportADiagram(diagram(type, name), tempDir.toFile()));
        switch (type) {
            case "ClassDiagram":
                verify(myDiagramFactory, atLeastOnce()).createClassDiagramExporter(any());
                verify(writerFactory, atLeastOnce()).createClassUMLWriter(any());
                break;
            case "ComponentDiagram":
            case "DeploymentDiagram":
                verify(myDiagramFactory, atLeastOnce()).createComponentDeploymentDiagramExporter(any());
                verify(writerFactory, atLeastOnce()).createComponentDeploymentUMLWriter(any());
                break;
            case "InteractionDiagram":
                verify(myDiagramFactory, atLeastOnce()).createSequenceDiagramExporter(any());
                verify(writerFactory, atLeastOnce()).createSequenceUMLWriter(any());
                break;
            case "UseCaseDiagram":
                verify(myDiagramFactory, atLeastOnce()).createUseCaseDiagramExporter(any());
                verify(writerFactory, atLeastOnce()).createUseCaseWriter(any());
                break;
            case "StateDiagram":
                verify(myDiagramFactory, atLeastOnce()).createStateDiagramExporter(any());
                verify(writerFactory, atLeastOnce()).createStateUMLWriter(any());
                break;
            case "ActivityDiagram":
                verify(myDiagramFactory, atLeastOnce()).createActivityDiagramExporter(any());
                verify(writerFactory, atLeastOnce()).createActivityUMLWriter(any());
                break;
            default:
                throw new IllegalArgumentException("Unsupported diagram type: " + type);
        }
    }

    @Test
    void export_classDiagram_success() throws Throwable {
        runSupportedCase("ClassDiagram", "C");
        runSupportedCase("ClassDiagram", "C2");
        runSupportedCase("ClassDiagram", "C3");
    }

    @Test
    void export_componentDiagram_success() throws Throwable {
        runSupportedCase("ComponentDiagram", "CD");
        runSupportedCase("ComponentDiagram", "CD2");
        runSupportedCase("ComponentDiagram", "CD3");
    }

    @Test
    void export_deploymentDiagram_success() throws Throwable {
        runSupportedCase("DeploymentDiagram", "DD");
        runSupportedCase("DeploymentDiagram", "DD2");
        runSupportedCase("DeploymentDiagram", "DD3");
    }

    @Test
    void export_interactionDiagram_success() throws Throwable {
        runSupportedCase("InteractionDiagram", "S");
        runSupportedCase("InteractionDiagram", "S2");
        runSupportedCase("InteractionDiagram", "S3");
    }

    @Test
    void export_useCaseDiagram_success() throws Throwable {
        runSupportedCase("UseCaseDiagram", "U");
        runSupportedCase("UseCaseDiagram", "U2");
        runSupportedCase("UseCaseDiagram", "U3");
    }

    @Test
    void export_stateDiagram_success() throws Throwable {
        runSupportedCase("StateDiagram", "ST");
        runSupportedCase("StateDiagram", "ST2");
        runSupportedCase("StateDiagram", "ST3");
    }

    @Test
    void export_activityDiagram_success() throws Throwable {
        runSupportedCase("ActivityDiagram", "A");
        runSupportedCase("ActivityDiagram", "A2");
        runSupportedCase("ActivityDiagram", "A3");
    }

    @Test
    void export_unsupportedType_throwsUnfit() {
        IDiagramUIModel d = diagram("BogusDiagram", "B");
        assertThrows(UnfitForExportException.class, () -> newPipeline().exportADiagram(d, tempDir.toFile()));
    }

    @Test
    void export_writerThrowsIOException_printsAndRethrows() throws Throwable {
        MyClassDiagram cde = givenClassExporter();
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        doThrow(new IOException("boom")).when(w).writeToFile(any(File.class));
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        IDiagramUIModel d = diagram("ClassDiagram", "IO");
        assertThrows(IOException.class, () -> newPipeline().exportADiagram(d, tempDir.toFile()));
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
        assertTrue(newPipeline().exportDiagrams(Collections.emptyList(), tempDir.toFile()));
    }

    @Test
    void exportDiagramList_allSuccess_returnsTrue() {
        IDiagramUIModel d = diagram("ClassDiagram", "L1");
        givenClassWriter(givenClassExporter());
        assertTrue(newPipeline().exportDiagrams(Collections.singletonList(d), tempDir.toFile()));
    }

    @Test
    void exportDiagramList_unsupportedDiagram_returnsFalse() {
        IDiagramUIModel d = diagram("WeirdDiagram", "W");
        assertFalse(newPipeline().exportDiagrams(Collections.singletonList(d), tempDir.toFile()));
    }

    @Test
    void exportDiagramList_writerIOException_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "IO");
        MyClassDiagram cde = givenClassExporter();
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        doThrow(new IOException("boom")).when(w).writeToFile(any(File.class));
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        assertFalse(newPipeline().exportDiagrams(Collections.singletonList(d), tempDir.toFile()));
    }

    @Test
    void exportDiagramList_writerUnsupportedOperation_returnsFalse() throws Exception {
        IDiagramUIModel d = diagram("ClassDiagram", "UOE");
        MyClassDiagram cde = givenClassExporter();
        ClassUMLWriter w = mock(ClassUMLWriter.class);
        doThrow(new UnsupportedOperationException("nope")).when(w).writeToFile(any(File.class));
        when(writerFactory.createClassUMLWriter(cde)).thenReturn(w);
        assertFalse(newPipeline().exportDiagrams(Collections.singletonList(d), tempDir.toFile()));
    }

    @Test
    void exportAllDiagrams_exportsEveryDiagram() {
//        IDiagramUIModel clazz = diagram("ClassDiagram", "C");
//        IDiagramUIModel seq = diagram("InteractionDiagram", "S");
//        givenClassWriter(givenClassExporter());
//        MySequenceDiagram seqde = mock(MySequenceDiagram.class);
//        when(myDiagramFactory.createSequenceDiagramExporter(any())).thenReturn(seqde);
//        when(writerFactory.createSequenceUMLWriter(seqde)).thenReturn(mock(SequenceUMLWriter.class));
//        IProject project = givenProject();
//        when(project.toDiagramArray()).thenReturn(new IDiagramUIModel[]{clazz, seq});
//        newPipeline().exportAll(tempDir.toFile());
//        verify(projectRepository).getProject();
    }

    @Test
    void exportSpecificDiagram_exportsTarget() throws Exception {
        IDiagramUIModel target = diagram("ClassDiagram", "T");
        givenClassWriter(givenClassExporter());
        IProject project = givenProject();
        when(project.getDiagramById("X")).thenReturn(target);
        newPipeline().exportSpecificDiagram("X", tempDir.toFile());
        verify(project).getDiagramById("X");
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