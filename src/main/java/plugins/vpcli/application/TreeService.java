package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.VPMetaModelFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;

import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.myuml.mycommon.ElementType.*;

public class TreeService {

    private final ProjectRepository projectRepository;
    private final VPMetaModelFactory vpMetaModelFactory;
    private final TreeElementVisitorFactory treeVisitorFactory;

    public TreeService(ProjectRepository projectRepository
        , VPMetaModelFactory vpMetaModelFactory
        , TreeElementVisitorFactory treeVisitorFactory) {

        this.projectRepository = projectRepository;
        this.vpMetaModelFactory = vpMetaModelFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() throws IOException {
        var project = projectRepository.getProject();

        var vpStruct = vpMetaModelFactory.create(project, treeVisitorFactory);
        vpStruct.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , ALL_DIAGRAMS));

        vpStruct.accept(treeVisitorFactory);
    }
}
