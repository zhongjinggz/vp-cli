package vpcli.plugin.drivenadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

@Singleton
public class VPProjectRepository {
    @Nullable private IProject vpProject;

    @Inject
    public VPProjectRepository() {
    }

    public IProject get() {
        if (vpProject == null) {
            vpProject = ApplicationManager.instance().getProjectManager().getProject();
        }
        return vpProject;
    }
}
