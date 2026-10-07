package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.vpstruct.VPStructFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;

import java.io.IOException;
import java.util.List;

import static plugins.vpcli.domain.vpstruct.ElementType.*;

public class TreeService {

    private final ProjectRepository projectRepository;
    private final VPStructFactory vpStructFactory;
    private final TreeVisitorFactory treeVisitorFactory;

    public TreeService(ProjectRepository projectRepository
        , VPStructFactory vpStructFactory
        , TreeVisitorFactory treeVisitorFactory) {

        this.projectRepository = projectRepository;
        this.vpStructFactory = vpStructFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() throws IOException {
        var project = projectRepository.getProject();

        var vpStruct = vpStructFactory.create(project, treeVisitorFactory);
        vpStruct.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , ALL_DIAGRAMS));

        vpStruct.accept(treeVisitorFactory);
    }
}
