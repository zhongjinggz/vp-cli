package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;

public class ViProjectFactory {

    private ViElementFactory viElementFactory;

    public ViProjectFactory(ViElementFactory viElementFactory) {
        this.viElementFactory = viElementFactory;
    }
    public ViProject of(IProject project) {
        return new ViProject(project, viElementFactory);
    }
}

