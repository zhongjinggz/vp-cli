package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class TreeService {

    private final VPProjectRepository vpProjectRepository;
    private final ViProjectFactory viProjectFactory;
    private final TreeElementVisitorFactory treeVisitorFactory;

    public TreeService(VPProjectRepository vpProjectRepository
        , ViProjectFactory viProjectFactory
        , TreeElementVisitorFactory treeVisitorFactory) {

        this.vpProjectRepository = vpProjectRepository;
        this.viProjectFactory = viProjectFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() throws IOException {
        var project = vpProjectRepository.getProject();

        var viProject = viProjectFactory.create(project);
        viProject.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , DIAGRAM));

        viProject.accept(treeVisitorFactory);
    }
}
