package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeVisitorCreator;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectCreator;
import plugins.vpcli.drivenadapter.VPProjectRepository;

import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class TreeService {

    private final VPProjectRepository vpProjectRepository;
    private final ViProjectCreator viProjectCreator;
    private final TreeVisitorCreator treeVisitorCreator;

    public TreeService(VPProjectRepository vpProjectRepository
        , ViProjectCreator viProjectCreator
        , TreeVisitorCreator treeVisitorCreator) {

        this.vpProjectRepository = vpProjectRepository;
        this.viProjectCreator = viProjectCreator;
        this.treeVisitorCreator = treeVisitorCreator;
    }

    public void tree() throws IOException {
        var project = vpProjectRepository.fromVisualParadigm();

        var viProject = viProjectCreator.of(project);
        viProject.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , DIAGRAM));

        viProject.accept(treeVisitorCreator);
    }
}
