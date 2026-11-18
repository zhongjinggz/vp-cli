package vpcli.plugin.drivingadapter;

import org.slf4j.LoggerFactory;
import vpcli.plugin.util.BugException;

import java.util.Arrays;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CLIController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CLIController.class);

    private final TreeCommand treeCommand;
    private final ExportCommand exportCommand;

    @Inject
    public CLIController(
        TreeCommand treeCommand,
        ExportCommand exportCommand
    ) {

        this.treeCommand = treeCommand;
        this.exportCommand = exportCommand;
    }

    public void invoke(String[] args) {
        var commandName = args[0];
        var commandArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (commandName) {
            case "tree":
                treeCommand.execute(commandArgs);
                break;
            case "export":
                exportCommand.execute(commandArgs);
                break;
            default:
                throw new BugException("wrong command '" + commandName + "'");
        }
    }

}