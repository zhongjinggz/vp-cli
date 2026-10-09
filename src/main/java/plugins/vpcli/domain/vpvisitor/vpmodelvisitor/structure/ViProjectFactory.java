package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;

public class ViProjectFactory {
    public ViProject of(IProject project) {
        // TODO VPElementFactory should be injected instead of new directly
        return new ViProject(project, new ViElementFactory());
    }
}

