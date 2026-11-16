package vpcli.plugin.application;

import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitor;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;
import vpcli.plugin.drivenadapter.VPProjectRepository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TreeService {

    private final VPProjectRepository vpProjectRepository;
    private final ViProjectFactory viProjectFactory;

    @Inject
    public TreeService(VPProjectRepository vpProjectRepository,
                       ViProjectFactory viProjectFactory
    ) {
        // TODO inject vpProjectRepository to ViProjectFactory
        this.vpProjectRepository = vpProjectRepository;
        this.viProjectFactory = viProjectFactory;
    }

    public void tree() {
        var viProject = viProjectFactory.build(vpProjectRepository.get());
        viProject.accept(rootTreeVisitor());
    }

    // Mock framework can spy visitor here
    @NonNull TreeVisitor rootTreeVisitor() {
        return TreeVisitor.root();
    }
}
