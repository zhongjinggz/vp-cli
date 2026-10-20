package plugins.vpcli.application;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

import jakarta.inject.Singleton;

@Singleton
public class ExportService {

    private final VPProjectRepository getIProject;
    private final ViProjectFactory createViProject;
    private final ExportDiagramVisitorFactory exportDiagramVisitorCreator;

    public ExportService(VPProjectRepository vpProjectRepository,
                         ViProjectFactory viProjectFactory,
                         ExportDiagramVisitorFactory exportDiagramVisitorCreator
    ) {

        this.getIProject = vpProjectRepository;
        this.createViProject = viProjectFactory;
        this.exportDiagramVisitorCreator = exportDiagramVisitorCreator;
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

    public void exportSpecificDiagram(String target, File exportLocation) {
        // TO BE DEV
    }

}