package plugins.vpcli.drivenadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class VPProjectRepository {
    private IProject vpProject;

    @Inject
    public VPProjectRepository() {
    }

    public IProject fromVisualParadigm() {
        if (vpProject == null) {
            vpProject = ApplicationManager.instance().getProjectManager().getProject();
        }
        return vpProject;
    }
}
