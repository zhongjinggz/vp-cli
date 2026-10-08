package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.ExportElementVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class ExportService {

    private final ProjectRepository projectRepository;
    private final ViProjectFactory viProjectFactory;
    private final ExportElementVisitorFactory exportVisitorFactory;

    public ExportService(ProjectRepository projectRepository
        , ViProjectFactory viProjectFactory
        , ExportElementVisitorFactory exportVisitorFactory) {

        this.projectRepository = projectRepository;
        this.viProjectFactory = viProjectFactory;
        this.exportVisitorFactory = exportVisitorFactory;
    }

    public void exportAll(File exportLocation) throws IOException {

        var project = projectRepository.getProject();

        exportVisitorFactory.setRootDir(exportLocation);
        var struct = viProjectFactory.create(project);
        struct.setElementTypes(List.of(
            PACKAGE
            , MODEL
            , DIAGRAM));

        struct.accept(this.exportVisitorFactory);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
//        IDiagramUIModel targetDiagram = projectRepository.getProject().getDiagramById(target);
//        this.exportADiagram(targetDiagram, exportLocation);
    }

}