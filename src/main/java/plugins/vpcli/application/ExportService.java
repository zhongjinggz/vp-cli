package plugins.vpcli.application;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.vpmodelvisitor.structure.ViProject;
import plugins.vpcli.domain.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.myelement.MyElementType.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportService {

    private final VPProjectRepository getIProject;
    private final ViProjectFactory createViProject;
    private final ExportDiagramVisitorFactory exportDiagramVisitorCreator;

    @Inject
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