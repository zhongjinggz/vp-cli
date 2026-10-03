package plugins.vpcli.application;

import com.vp.plugin.diagram.IDiagramUIModel;

import plugins.vpcli.domain.mydiagram.*;
import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.application.writers.*;
import plugins.vpcli.drivenadapter.TreeDirMaker;
import plugins.vpcli.util.UnfitForExportException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExportService {

    private final TreeConverter convertPackage;
    private final ProjectRepository projectRepository;
    private final MyDiagramFactory myDiagramFactory;
    private final WriterFactory writerFactory;
    private final FileFactory fileFactory;
    private final TreeDirMaker makeDir;

    public ExportService(TreeConverter treeConverter
        , ProjectRepository projectRepository
        , MyDiagramFactory myDiagramFactory
        , WriterFactory writerFactory
        , FileFactory fileFactory
        , TreeDirMaker treeDirMaker) {

        this.convertPackage = treeConverter;
        this.projectRepository = projectRepository;
        this.myDiagramFactory = myDiagramFactory;
        this.writerFactory = writerFactory;
        this.fileFactory = fileFactory;
        this.makeDir = treeDirMaker;
    }

    public void exportADiagram(IDiagramUIModel vpDiagram
        , File exportLocation) throws IOException, UnfitForExportException {

        String diagramType = vpDiagram.getType();
        String diagramTitle = vpDiagram.getName();
        File outputFile = createOutputFile(diagramTitle, "uml", exportLocation);

        try {
            switch (diagramType) {
                case "ClassDiagram":
                    exportClassDiagram(vpDiagram, outputFile);
                    break;

                case "ComponentDiagram":
                case "DeploymentDiagram":
                    MyComponentDeploymentDiagram exporter = myDiagramFactory.createComponentDeploymentDiagramExporter(vpDiagram);
                    exporter.extract();
                    ComponentDeploymentUMLWriter componentWriter = writerFactory.createComponentDeploymentUMLWriter(exporter);
                    componentWriter.writeToFile(outputFile);
                    break;

                case "InteractionDiagram":
                    MySequenceDiagram seqde = myDiagramFactory.createSequenceDiagramExporter(vpDiagram);
                    seqde.extract();
                    SequenceUMLWriter sequenceWriter = writerFactory.createSequenceUMLWriter(seqde);
                    sequenceWriter.writeToFile(outputFile);
                    break;

                case "UseCaseDiagram":
                    MyUseCaseDiagram ucde = myDiagramFactory.createUseCaseDiagramExporter(vpDiagram);
                    ucde.extract();
                    UseCaseWriter useCaseWriter = writerFactory.createUseCaseWriter(ucde);
                    useCaseWriter.writeToFile(outputFile);
                    break;
                case "StateDiagram":
                    MyStateDiagram stde = myDiagramFactory.createStateDiagramExporter(vpDiagram);
                    stde.extract();
                    StateUMLWriter stateUMLWriter = writerFactory.createStateUMLWriter(stde);
                    stateUMLWriter.writeToFile(outputFile);
                    break;
                case "ActivityDiagram":
                    MyActivityDiagram acde = myDiagramFactory.createActivityDiagramExporter(vpDiagram);
                    acde.extract();
                    ActivityUMLWriter activityUMLWriter = writerFactory.createActivityUMLWriter(acde);
                    activityUMLWriter.writeToFile(outputFile);
                    break;
                default:
                    throw new UnfitForExportException("Error: " + diagramType + " not supported for export yet.");
            }
        } catch (IOException | UnfitForExportException ex) {
            //TODO 统一异常处理
            throw ex;

        }
    }

    private void exportClassDiagram(IDiagramUIModel vpDiagram
        , File outputFile) throws IOException {
        MyClassDiagram exporter = myDiagramFactory.createClassDiagramExporter(vpDiagram);
        exporter.extract();
        ClassUMLWriter classWriter = writerFactory.createClassUMLWriter(exporter);
        classWriter.writeToFile(outputFile);
    }

    File createOutputFile(String title, String contentType, File exportLocation) throws IOException {
        StringBuilder fileName = new StringBuilder();
        // 放行所有语言的字母和数字（中文文件名）；空格、符号及 Windows 保留字符仍转下划线
        fileName.append(title.replaceAll("[^\\p{L}\\p{N}]", "_"));
        if (contentType.equals("json")) fileName.append("_semantics");
        fileName.append(".puml");
        File outputFile = fileFactory.createFile(exportLocation, fileName.toString());
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
        }
        return outputFile;
    }

    public boolean exportDiagrams(List<IDiagramUIModel> vpDiagrams
        , File exportLocation) {

        boolean allSuccessful = true;

        for (var aDiagram : vpDiagrams) {
            try {
                exportADiagram(aDiagram, exportLocation);
            } catch (IOException | UnsupportedOperationException | UnfitForExportException ex) {
                //TODO 统一异常处理
                allSuccessful = false;
            }
        }

        return allSuccessful;
    }

    public void exportAll(File exportLocation) {

        var project = projectRepository.getProject();

        var packages = convertPackage.fromVPProject(project);
        makeDir.forPackages(packages, exportLocation);

        var vpDiagrams = project.toDiagramArray();

        this.exportDiagrams(Arrays.asList(vpDiagrams), exportLocation);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
        IDiagramUIModel targetDiagram = projectRepository.getProject().getDiagramById(target);
        this.exportADiagram(targetDiagram, exportLocation);
    }

}