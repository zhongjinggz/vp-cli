package vpcli.plugin.drivingadapter;

import org.slf4j.LoggerFactory;
import vpcli.plugin.util.BugException;

import java.util.Arrays;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CLIController {
    @SuppressWarnings("unused")
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CLIController.class);

    private final TreeCommandFactory treeCommandFactory;
    private final ExportCommandFactory exportCommandFactory;

    @Inject
    public CLIController(
        TreeCommandFactory treeCommandFactory,
        ExportCommandFactory exportCommandFactory
    ) {

        this.treeCommandFactory = treeCommandFactory;
        this.exportCommandFactory = exportCommandFactory;
    }

    public void invoke(String[] args) {
        var commandName = args[0];
        var commandArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (commandName) {
            case "tree":
                treeCommandFactory.build(commandArgs).execute();
                break;
            case "export":
                exportCommandFactory.build(commandArgs).execute();
                break;
            default:
                throw new BugException("wrong command '" + commandName + "'");
        }
    }

}