package vpcli.plugin.application;

import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitorFactory;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;
import vpcli.plugin.drivenadapter.VPProjectRepository;

import java.io.IOException;
import java.util.List;

import static vpcli.plugin.domain.vpmodelvisitor.ViElementType.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TreeService {

    private final VPProjectRepository vpProjectRepository;
    private final ViProjectFactory viProjectFactory;
    private final TreeVisitorFactory treeVisitorFactory;

    @Inject
    public TreeService(VPProjectRepository vpProjectRepository,
                       ViProjectFactory viProjectFactory,
                       TreeVisitorFactory treeVisitorFactory
    ) {
        this.vpProjectRepository = vpProjectRepository;
        this.viProjectFactory = viProjectFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() throws IOException {
        var project = vpProjectRepository.get();

        var viProject = viProjectFactory.build(project);
        viProject.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , DIAGRAM));

        viProject.accept(treeVisitorFactory);
    }

    public void tree2() {
        var project = vpProjectRepository.get();

        var viProject = viProjectFactory.build(project);

        TreeVisitor treeVisitor = TreeVisitor.root();
        viProject.accept(treeVisitor);

    }
}
