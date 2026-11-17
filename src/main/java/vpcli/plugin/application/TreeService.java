package vpcli.plugin.application;

import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitor;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitorFactory;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.TravellerFactory;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.Traveller;

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
        var viProject = viProjectFactory.build();
        viProject.accept(rootTreeVisitor());
    }

    // Mock framework can spy visitor here
    @NonNull TreeVisitor rootTreeVisitor() {
        return TreeVisitor.root();
    }


    public void tree2() {
        var viProject = viProjectFactory.build();
        Traveller traveller = travellerFactory.buildTraveller(treeVisitorFactory);
        traveller.travel(viProject);
    }
}
