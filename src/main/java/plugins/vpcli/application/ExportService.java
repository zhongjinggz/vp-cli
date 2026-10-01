package plugins.vpcli.application;

import com.vp.plugin.diagram.IDiagramUIModel;

import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.domain.myuml.myproject.MyConverter;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.application.exporter.*;
import plugins.vpcli.application.writers.*;
import plugins.vpcli.drivenadapter.TreeDirMaker;
import plugins.vpcli.util.UnfitForExportException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExportService {

    private final MyConverter convert;
    private final ProjectRepository projectRepository;
    private final ExporterFactory exporterFactory;
    private final WriterFactory writerFactory;
    private final FileFactory fileFactory;
    private final TreeDirMaker makeDir;

    public ExportService(MyConverter myConverter
        , ProjectRepository projectRepository
        , ExporterFactory exporterFactory
        , WriterFactory writerFactory
        , FileFactory fileFactory
        , TreeDirMaker treeDirMaker) {

        this.convert = myConverter;
        this.projectRepository = projectRepository;
        this.exporterFactory = exporterFactory;
        this.writerFactory = writerFactory;
        this.fileFactory = fileFactory;
        this.makeDir = treeDirMaker;
    }

    public void export(IDiagramUIModel diagram, File exportLocation) throws IOException, UnfitForExportException {
        String diagramType = diagram.getType();
        String diagramTitle = diagram.getName();
        File outputFile = createOutputFile(diagramTitle, "uml", exportLocation);

        try {
            switch (diagramType) {
                case "ClassDiagram":
                    ClassDiagramExporter cde = exporterFactory.createClassDiagramExporter(diagram);
                    cde.extract();
                    ClassUMLWriter classWriter = writerFactory.createClassUMLWriter(cde);
                    classWriter.writeToFile(outputFile);
                    break;

                case "ComponentDiagram":
                case "DeploymentDiagram":
                    ComponentDeploymentDiagramExporter exporter = exporterFactory.createComponentDeploymentDiagramExporter(diagram);
                    exporter.extract();
                    ComponentDeploymentUMLWriter componentWriter = writerFactory.createComponentDeploymentUMLWriter(exporter);
                    componentWriter.writeToFile(outputFile);
                    break;

                case "InteractionDiagram":
                    SequenceDiagramExporter seqde = exporterFactory.createSequenceDiagramExporter(diagram);
                    seqde.extract();
                    SequenceUMLWriter sequenceWriter = writerFactory.createSequenceUMLWriter(seqde);
                    sequenceWriter.writeToFile(outputFile);
                    break;

                case "UseCaseDiagram":
                    UseCaseDiagramExporter ucde = exporterFactory.createUseCaseDiagramExporter(diagram);
                    ucde.extract();
                    UseCaseWriter useCaseWriter = writerFactory.createUseCaseWriter(ucde);
                    useCaseWriter.writeToFile(outputFile);
                    break;
                case "StateDiagram":
                    StateDiagramExporter stde = exporterFactory.createStateDiagramExporter(diagram);
                    stde.extract();
                    StateUMLWriter stateUMLWriter = writerFactory.createStateUMLWriter(stde);
                    stateUMLWriter.writeToFile(outputFile);
                    break;
                case "ActivityDiagram":
                    ActivityDiagramExporter acde = exporterFactory.createActivityDiagramExporter(diagram);
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

    public boolean exportDiagramList(List<IDiagramUIModel> selectedDiagrams, File exportLocation) {
        boolean allSuccessful = true;
        for (IDiagramUIModel activeDiagram : selectedDiagrams) {
            try {
                export(activeDiagram, exportLocation);
            } catch (IOException | UnsupportedOperationException | UnfitForExportException ex) {
                //TODO 统一异常处理
                allSuccessful = false;
            }
        }

        return allSuccessful;
    }

    public void exportAll(File exportLocation) {

        var project = projectRepository.getProject();

        var packages = convert.fromVPElementsToPackages(project.toModelElementArray());
		makeDir.forPackages(packages, exportLocation);

        var allDiagrams = project.toDiagramArray();

        this.exportDiagramList(Arrays.asList(allDiagrams), exportLocation);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
        IDiagramUIModel targetDiagram = projectRepository.getProject().getDiagramById(target);
        this.export(targetDiagram, exportLocation);
    }

}