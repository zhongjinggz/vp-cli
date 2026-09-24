package plugins.plantUML;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;

public class ProjectManagerFactory {
    public ProjectManager getProjectManager() {
        return ApplicationManager.instance().getProjectManager();
    }
}
