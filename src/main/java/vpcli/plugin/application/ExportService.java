package vpcli.plugin.application;

import vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor.ExportVisitorFactory;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.Traveller;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.TravellerFactory;

import java.io.File;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_MODEL;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_PACKAGE;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportService {

    private final ViProjectFactory viProjectFactory;
    private final ExportVisitorFactory exportVisitorFactory;
    private final TravellerFactory travellerFactory;

    @Inject
    public ExportService(
        ViProjectFactory viProjectFactory,
        ExportVisitorFactory exportVisitorFactory,
        TravellerFactory travellerFactory
    ) {

        this.viProjectFactory = viProjectFactory;
        this.exportVisitorFactory = exportVisitorFactory;
        this.travellerFactory = travellerFactory;
    }

    public void export(File exportLocation) {

        String[] vpElementTypes = new String[]{
            MODEL_TYPE_MODEL,
            MODEL_TYPE_PACKAGE
        };

        var viProject = viProjectFactory.build();

        this.exportVisitorFactory.setRootDir(exportLocation);
        Traveller traveller = travellerFactory.buildTraveller(
            this.exportVisitorFactory,
            vpElementTypes
        );
        traveller.travel(viProject);
    }

}
