package plugins.vpcli.drivingadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class HelpController implements VPActionController {

    @Override
    public void performAction(VPAction action) {

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance()
                .getViewManager()
                .getRootFrame()
            , "Refactor TreeService -  10:07"
        );
    }

    @Override
    public void update(VPAction action) {
    }

}
