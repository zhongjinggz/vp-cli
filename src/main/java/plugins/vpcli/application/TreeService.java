package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class TreeService {

    private final VPProjectRepository vpProjectRepository;
    private final ViProjectFactory viProjectFactory;
    private final TreeVisitorFactory treeVisitorCreator;

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
