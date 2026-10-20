package plugins.vpcli.application;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorCreator;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectCreator;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class ExportService {

    private final VPProjectRepository getIProject;
    private final ViProjectCreator createViProject;
    private final ExportDiagramVisitorCreator exportDiagramVisitorCreator;
    private MyProjectFactory getMyProject;

    public ExportService(VPProjectRepository vpProjectRepository,
                         ViProjectCreator viProjectCreator,
                         ExportDiagramVisitorCreator exportDiagramVisitorCreator,
                         MyProjectFactory myProjectFactory) {

        this.getIProject = vpProjectRepository;
        this.createViProject = viProjectCreator;
        this.exportDiagramVisitorCreator = exportDiagramVisitorCreator;
        this.getMyProject = myProjectFactory;
    }

    public void exportAll(File exportLocation) throws IOException {

        var vpProject = getIProject.fromVisualParadigm();

        var viProject = getViProject(vpProject);
        viProject.setElementTypes(List.of(
            PACKAGE
            , MODEL
            , DIAGRAM));

        this.exportDiagramVisitorCreator.setRootDir(exportLocation);
        viProject.accept(this.exportDiagramVisitorCreator);
    }

    private ViProject getViProject(IProject vpProject) {
        return createViProject.of(vpProject);
    }

    public void exportSpecificDiagram(String target, File exportLocation) throws IOException {
//        IDiagramUIModel targetDiagram = projectRepository.getProject().getDiagramById(target);
//        this.exportADiagram(targetDiagram, exportLocation);
    }

}