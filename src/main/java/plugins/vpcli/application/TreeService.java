package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;

import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class TreeService {

    private final ProjectRepository projectRepository;
    private final ViProjectFactory viProjectFactory;
    private final TreeElementVisitorFactory treeVisitorFactory;

    public TreeService(ProjectRepository projectRepository
        , ViProjectFactory viProjectFactory
        , TreeElementVisitorFactory treeVisitorFactory) {

        this.projectRepository = projectRepository;
        this.viProjectFactory = viProjectFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() throws IOException {
        var project = projectRepository.getProject();

        var viProject = viProjectFactory.create(project);
        viProject.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , DIAGRAM));

        viProject.accept(treeVisitorFactory);
    }
}
