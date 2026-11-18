package vpcli.plugin.drivingadapter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.application.TreeService;

@Singleton
public class TreeCommand {
    private final TreeService treeService;

    @Inject
    public TreeCommand(TreeService treeService) {
        this.treeService = treeService;
    }

    public void execute(String[] args) {
        this.treeService.tree();
    }
}
