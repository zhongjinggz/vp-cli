package vpcli.plugin.drivingadapter;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import vpcli.plugin.application.TreeService;

public class TreeCommand {
    private final TreeService treeService;

    @AssistedInject
    public TreeCommand(
        TreeService treeService,
        @Assisted String[] args
    ) {
        this.treeService = treeService;
    }

    public void execute() {
        this.treeService.tree();
    }
}
