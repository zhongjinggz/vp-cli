package vpcli.plugin.application;

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
    private final TreeVisitorFactory treeVisitorCreator;

    @Inject
    public TreeService(VPProjectRepository vpProjectRepository
        , ViProjectFactory viProjectFactory
        , TreeVisitorFactory treeVisitorCreator) {

        this.vpProjectRepository = vpProjectRepository;
        this.viProjectFactory = viProjectFactory;
        this.treeVisitorCreator = treeVisitorCreator;
    }

    public void tree() throws IOException {
        var project = vpProjectRepository.fromVisualParadigm();

        var viProject = viProjectFactory.of(project);
        viProject.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , DIAGRAM));

        viProject.accept(treeVisitorCreator);
    }
}
