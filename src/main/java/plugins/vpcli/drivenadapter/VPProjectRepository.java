package plugins.vpcli.drivenadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

public class VPProjectRepository {
    private IProject vpProject;

    public IProject fromVisualParadigm() {
        if (vpProject == null) {
            vpProject = ApplicationManager.instance().getProjectManager().getProject();
        }
        return vpProject;
    }
}
