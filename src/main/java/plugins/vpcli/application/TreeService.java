package plugins.vpcli.application;

import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.vpstruct.ElementType;
import plugins.vpcli.domain.vpstruct.VPStructFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreePrinter;

import java.util.List;

import static plugins.vpcli.domain.vpstruct.ElementType.*;

public class TreeService {

    private final ProjectRepository projectRepository;
    private final TreeConverter convert;
    private final TreePrinter treePrinter;
    private final VPStructFactory vpStructFactory;
    private final TreeVisitorFactory treeVisitorFactory;

    public TreeService(ProjectRepository projectRepository
        , TreeConverter convert
        , TreePrinter treePrinter
        , VPStructFactory vpStructFactory
        , TreeVisitorFactory treeVisitorFactory) {

        this.projectRepository = projectRepository;
        this.convert = convert;
        this.treePrinter = treePrinter;
        this.vpStructFactory = vpStructFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() {
        var project = projectRepository.getProject();

        var vpStruct = vpStructFactory.create(project, treeVisitorFactory);
        vpStruct.setElementTypes(
            List.of(PACKAGE
                , MODEL
                , CLASS
                , ALL_DIAGRAMS));

        vpStruct.accept();
    }
}
