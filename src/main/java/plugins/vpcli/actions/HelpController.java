package plugins.vpcli.actions;

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
            , "Move menu and change menu name 16:55"
        );
    }

    @Override
    public void update(VPAction action) {
    }

}
