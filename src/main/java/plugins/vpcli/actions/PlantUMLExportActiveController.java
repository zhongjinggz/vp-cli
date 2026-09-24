package plugins.vpcli.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class PlantUMLExportActiveController implements VPActionController {

    @Override
    public void performAction(VPAction action) {

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance()
                .getViewManager()
                .getRootFrame()
            , "vp-cli update package name and plugin id to 'plugins.vpcli' 11:39"
        );
    }

    @Override
    public void update(VPAction action) {
    }

}
