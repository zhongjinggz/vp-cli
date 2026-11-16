package vpcli.plugin.application;

import vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor.ExportDiagramVisitorFactory;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;
import vpcli.plugin.drivenadapter.VPProjectRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static vpcli.plugin.domain.vpmodelvisitor.ViElementType.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportService {

    private final VPProjectRepository getProject;
    private final ViProjectFactory ViProjectFactory;
    private final ExportDiagramVisitorFactory exportDiagramVisitorFactory;

    @Inject
    public ExportService(VPProjectRepository vpProjectRepository,
                         ViProjectFactory viProjectFactory,
                         ExportDiagramVisitorFactory exportDiagramVisitorFactory
    ) {

        this.getProject = vpProjectRepository;
        this.ViProjectFactory = viProjectFactory;
        this.exportDiagramVisitorFactory = exportDiagramVisitorFactory;
    }

    public void exportAll(File exportLocation) throws IOException {

        var vpProject = getProject.get();

        var viProject = ViProjectFactory.build(vpProject);
        viProject.setElementTypes(List.of(
            PACKAGE
            , MODEL
            , DIAGRAM));

        this.exportDiagramVisitorFactory.setRootDir(exportLocation);
        viProject.accept(this.exportDiagramVisitorFactory);
    }

}