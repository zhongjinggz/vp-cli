package plugins.plantUML.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class PlantUMLExportActiveController implements VPActionController {

    @Override
    public void performAction(VPAction action) {

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance().getViewManager().getRootFrame(), "vp-cli 改为依赖注入 22:43");
    }

    @Override
    public void update(VPAction action) {
    }

}
