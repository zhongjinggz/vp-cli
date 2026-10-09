package plugins.vpcli.drivenadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

public class VPProjectRepository {

    public IProject fromVisualParadigm() {
        return ApplicationManager.instance().getProjectManager().getProject();
    }
}
