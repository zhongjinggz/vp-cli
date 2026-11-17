package vpcli.plugin.application;

import vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor.ExportDiagramVisitorFactory;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;
import vpcli.plugin.drivenadapter.VPProjectRepository;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.Traveller;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.TravellerFactory;

import java.io.File;
import java.io.IOException;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_MODEL;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_PACKAGE;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportService {

    private final VPProjectRepository getProject;
    private final ViProjectFactory viProjectFactory;
    private final ExportDiagramVisitorFactory exportDiagramVisitorFactory;
    private final TravellerFactory travellerFactory;

    @Inject
    public ExportService(VPProjectRepository vpProjectRepository,
                         ViProjectFactory viProjectFactory,
                         ExportDiagramVisitorFactory exportDiagramVisitorFactory,
                         TravellerFactory travellerFactory
    ) {

        this.getProject = vpProjectRepository;
        this.viProjectFactory = viProjectFactory;
        this.exportDiagramVisitorFactory = exportDiagramVisitorFactory;
        this.travellerFactory = travellerFactory;
    }

    public void exportAll(File exportLocation) throws IOException {

        var vpProject = getProject.get();

        String[] vpElementTypes = new String[]{
            MODEL_TYPE_MODEL,
            MODEL_TYPE_PACKAGE
        };

        var viProject = viProjectFactory.build(vpProject);

        this.exportDiagramVisitorFactory.setRootDir(exportLocation);
        Traveller traveller = travellerFactory.buildTraveller(this.exportDiagramVisitorFactory, vpElementTypes);
        traveller.travel(viProject);
    }

}
