package plugins.vpcli.drivenadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

public class VPProjectRepository {

    public IProject getProject() {
        return ApplicationManager.instance().getProjectManager().getProject();
    }
}
