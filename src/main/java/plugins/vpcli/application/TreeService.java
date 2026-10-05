package plugins.vpcli.application;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.myproject.TreeConverter;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.vpstruct.VPStruct;
import plugins.vpcli.domain.vpstruct.VPStructElementFactory;
import plugins.vpcli.domain.vpstruct.VPVisitorFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivenadapter.TreePrinter;

public class TreeService {

    private final ProjectRepository projectRepository;
    private final TreeConverter convert;
    private final TreePrinter treePrinter;
    private final VPStructElementFactory vpStructElementFactory;
    private final TreeVisitorFactory treeVisitorFactory;

    public TreeService(ProjectRepository projectRepository
        , TreeConverter convert
        , TreePrinter treePrinter
        , VPStructElementFactory vpStructElementFactory
        , TreeVisitorFactory treeVisitorFactory) {

        this.projectRepository = projectRepository;
        this.convert = convert;
        this.treePrinter = treePrinter;
        this.vpStructElementFactory = vpStructElementFactory;
        this.treeVisitorFactory = treeVisitorFactory;
    }

    public void tree() {
//		var packages = convert.fromVPProject(projectRepository.getProject());
//		treePrinter.print(packages);

        var project = projectRepository.getProject();

        var vpStruct = new VPStruct(project
            , new VPStructElementFactory());

        vpStruct.setVisitorFactory(treeVisitorFactory);
        vpStruct.setElementTypes(new String[] {IModelElementFactory.MODEL_TYPE_PACKAGE
            ,IModelElementFactory.MODEL_TYPE_MODEL});

        vpStruct.accept();
    }
}
