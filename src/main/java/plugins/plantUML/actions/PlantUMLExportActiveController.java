package plugins.plantUML.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class PlantUMLExportActiveController implements VPActionController {

    @Override
    public void performAction(VPAction action) {

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance().getViewManager().getRootFrame(), "vp-cli 2026-9-23 22:13");
    }

    @Override
    public void update(VPAction action) {
    }

}
