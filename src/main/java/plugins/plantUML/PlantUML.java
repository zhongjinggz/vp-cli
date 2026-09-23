package plugins.plantUML;

import com.vp.plugin.*;
import plugins.plantUML.actions.CLIController;

public class PlantUML implements VPPlugin, VPPluginCommandLineSupport {
    
    CLIController cliController = new CLIController();

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
    }

    @Override
    public void unloaded() {
    }

    // CLI 入口：解析参数并分发到导入/导出逻辑
    @Override
    public void invoke(String[] args) {

        cliController.invoke(args);
    }
}
