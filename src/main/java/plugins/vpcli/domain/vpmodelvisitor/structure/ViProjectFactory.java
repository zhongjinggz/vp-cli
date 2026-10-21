package plugins.vpcli.domain.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import plugins.vpcli.domain.vpmodelvisitor.element.ViElementFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ViProjectFactory {

    private final ViElementFactory viElementFactory;

    @Inject
    public ViProjectFactory(ViElementFactory viElementFactory) {
        this.viElementFactory = viElementFactory;
    }
    public ViProject of(IProject project) {
        return new ViProject(project, viElementFactory);
    }
}

