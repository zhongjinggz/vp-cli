package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class ExportService {

    private final VPProjectRepository getIProject;
    private final ViProjectFactory createViProject;
    private final ExportDiagramVisitorFactory exportDiagramVisitorFactory;
    private MyProjectFactory getMyProject;

    public ExportService(VPProjectRepository vpProjectRepository,
                         ViProjectFactory viProjectFactory,
                         ExportDiagramVisitorFactory exportDiagramVisitorFactory,
                         MyProjectFactory myProjectFactory) {

        this.getIProject = vpProjectRepository;
        this.createViProject = viProjectFactory;
        this.exportDiagramVisitorFactory = exportDiagramVisitorFactory;
        this.getMyProject = myProjectFactory;
    }

    public void exportAll(File exportLocation) throws IOException {

        var vpProject = getIProject.fromVisualParadigm();
        var myProject = getMyProject.fromGlobal();

        var viProject = createViProject.of(vpProject, myProject);
        viProject.setElementTypes(List.of(
            PACKAGE
            , MODEL
            , DIAGRAM));

        exportDiagramVisitorFactory.setRootDir(exportLocation);
        viProject.accept(this.exportDiagramVisitorFactory);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
//        IDiagramUIModel targetDiagram = projectRepository.getProject().getDiagramById(target);
//        this.exportADiagram(targetDiagram, exportLocation);
    }

}