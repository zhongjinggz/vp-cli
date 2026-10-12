package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;

public class ViProjectCreator {

    private ViElementFactory viElementFactory;

    public ViProjectCreator(ViElementFactory viElementFactory) {
        this.viElementFactory = viElementFactory;
    }
    public ViProject of(IProject project) {
        // TODO VPElementFactory should be injected instead of new directly
        return new ViProject(project, viElementFactory);
    }
}

