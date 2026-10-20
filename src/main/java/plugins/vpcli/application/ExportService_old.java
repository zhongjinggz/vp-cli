package plugins.vpcli.application;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.application.writers_old.*;
import plugins.vpcli.domain.myuml.mydiagram_old.*;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.FileCreator;
import plugins.vpcli.drivenadapter.VPProjectRepository;
import plugins.vpcli.util.UnfitForExportException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportService_old {

    //private final TreeConverter convertPackage;
    private final VPProjectRepository vpProjectRepository;
    private final OldDiagramFactory myDiagramFactory;
    private final WriterCreator writerCreator;
    private final FileCreator fileCreator;
    private final ViProjectFactory viProjectFactory;
    //private final TreeDirMaker makeDir;

    public ExportService_old(VPProjectRepository vpProjectRepository
        , OldDiagramFactory myDiagramFactory
        , WriterCreator writerCreator
        , FileCreator fileCreator
        , ViProjectFactory viProjectFactory
        ) {

        this.vpProjectRepository = vpProjectRepository;
        this.myDiagramFactory = myDiagramFactory;
        this.writerCreator = writerCreator;
        this.fileCreator = fileCreator;
        this.viProjectFactory = viProjectFactory;
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
                    ComponentDeploymentUMLWriter componentWriter = writerCreator.createComponentDeploymentUMLWriter(exporter);
                    componentWriter.writeToFile(outputFile);
                    break;

                case "InteractionDiagram":
                    MySequenceDiagram seqde = myDiagramFactory.createSequenceDiagramExporter(vpDiagram);
                    seqde.extract();
                    SequenceUMLWriter sequenceWriter = writerCreator.createSequenceUMLWriter(seqde);
                    sequenceWriter.writeToFile(outputFile);
                    break;

                case "UseCaseDiagram":
                    MyUseCaseDiagram ucde = myDiagramFactory.createUseCaseDiagramExporter(vpDiagram);
                    ucde.extract();
                    UseCaseWriter useCaseWriter = writerCreator.createUseCaseWriter(ucde);
                    useCaseWriter.writeToFile(outputFile);
                    break;
                case "StateDiagram":
                    MyStateDiagram stde = myDiagramFactory.createStateDiagramExporter(vpDiagram);
                    stde.extract();
                    StateUMLWriter stateUMLWriter = writerCreator.createStateUMLWriter(stde);
                    stateUMLWriter.writeToFile(outputFile);
                    break;
                case "ActivityDiagram":
                    MyActivityDiagram acde = myDiagramFactory.createActivityDiagramExporter(vpDiagram);
                    acde.extract();
                    ActivityUMLWriter activityUMLWriter = writerCreator.createActivityUMLWriter(acde);
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
        OldClassDiagram diagram = new OldClassDiagram(vpDiagram);
        diagram.extract();
        ClassDiagramWriter classWriter = writerCreator.createClassDiagramWriter(diagram);
        classWriter.writeToFile(outputFile);
    }

    File createOutputFile(String title, String contentType, File exportLocation) throws IOException {
        StringBuilder fileName = new StringBuilder();
        // 放行所有语言的字母和数字（中文文件名）；空格、符号及 Windows 保留字符仍转下划线
        fileName.append(title.replaceAll("[^\\p{L}\\p{N}]", "_"));
        if (contentType.equals("json")) fileName.append("_semantics");
        fileName.append(".puml");
        File outputFile = fileCreator.under(exportLocation, fileName.toString());
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

    public void exportAll(File exportLocation) throws IOException {

//        var project = vpProjectRepository.fromVisualParadigm();
//
//        exportVisitorFactory.setRootDir(exportLocation);
//        var struct = viProjectFactory.of(project);
//        struct.setElementTypes(List.of(
//            PACKAGE
//            , MODEL
//            , DIAGRAM));
//
//        struct.accept(this.exportVisitorFactory);
//
//        var packages = convertPackage.fromVPProject(project);
//        makeDir.forPackages(packages, exportLocation);
//
//        var vpDiagrams = project.toDiagramArray();
//
//        this.exportDiagrams(Arrays.asList(vpDiagrams), exportLocation);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
        IDiagramUIModel targetDiagram = vpProjectRepository.fromVisualParadigm().getDiagramById(target);
        this.exportADiagram(targetDiagram, exportLocation);
    }

}