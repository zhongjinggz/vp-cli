package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.VPElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitorFactory;

public class VPMetaModelFactory {
    public VPMetaModel create(IProject project, VPElementVisitorFactory visitorFactory) {
        return new VPMetaModel(project, new VPElementFactory());
    }
}
