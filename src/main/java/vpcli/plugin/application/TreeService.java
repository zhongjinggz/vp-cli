package vpcli.plugin.application;

import vpcli.plugin.domain.vpmodelvisitor.element.viproject.ViProjectFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitorFactory;
import vpcli.plugin.domain.vpmodelvisitor.structure.TravellerFactory;
import vpcli.plugin.domain.vpmodelvisitor.structure.Traveller;

import static com.vp.plugin.model.factory.IModelElementFactory.*;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_USE_CASE;

@Singleton
public class TreeService {

    private final ViProjectFactory viProjectFactory;
    private final TravellerFactory travellerFactory;
    private final TreeVisitorFactory treeVisitorFactory;

    @Inject
    public TreeService(
        ViProjectFactory viProjectFactory,
        TravellerFactory travellerFactory,
        TreeVisitorFactory treeVisitorFactory
    ) {
        this.viProjectFactory = viProjectFactory;
        this.travellerFactory = travellerFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }


    public void tree() {
        String[] vpElementTypes = new String[]{
                MODEL_TYPE_MODEL,
                MODEL_TYPE_PACKAGE,
                MODEL_TYPE_CLASS,
                MODEL_TYPE_USE_CASE
            };

        var viProject = viProjectFactory.build();
        Traveller traveller = travellerFactory.buildTraveller(treeVisitorFactory, vpElementTypes);
        traveller.travel(viProject);
    }
}
