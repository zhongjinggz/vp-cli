package vpcli.plugin.application;

import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitor;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TreeService {

    private final ViProjectFactory viProjectFactory;

    @Inject
    public TreeService(ViProjectFactory viProjectFactory) {
        this.viProjectFactory = viProjectFactory;
    }

    public void tree() {
        var viProject = viProjectFactory.build();
        viProject.accept(rootTreeVisitor());
    }

    // Mock framework can spy visitor here
    @NonNull TreeVisitor rootTreeVisitor() {
        return TreeVisitor.root();
    }
}
