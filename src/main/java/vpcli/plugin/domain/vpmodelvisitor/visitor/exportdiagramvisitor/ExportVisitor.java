package vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import org.jspecify.annotations.Nullable;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import java.io.File;

public class ExportVisitor extends Visitor {
    @Nullable
    protected File path;

    File getPath() {
        return path;
    }
}
