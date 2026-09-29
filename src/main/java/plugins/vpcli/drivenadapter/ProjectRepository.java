package plugins.vpcli.drivenadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.model.IProject;

public class ProjectRepository {

    public IProject getProject() {
        return ApplicationManager.instance().getProjectManager().getProject();
    }
}
