package vpcli.plugin.domain.vpmodelvisitor.visitor.exportvisitor;

import org.jspecify.annotations.Nullable;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.util.FindBugs;

import java.io.File;

public class ExportVisitor extends Visitor {
    @Nullable
    protected File path;

    @SuppressWarnings("NullAway")
    File getPath() {
        FindBugs.requireNonNull(path, "exporting path should not be null!");
        return path;
    }
}
