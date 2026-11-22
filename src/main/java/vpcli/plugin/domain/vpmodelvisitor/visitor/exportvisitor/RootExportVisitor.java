package vpcli.plugin.domain.vpmodelvisitor.visitor.exportvisitor;

import java.io.File;

// Null Object Pattern
public class RootExportVisitor extends ExportVisitor {
    public RootExportVisitor(File path) {
        super.path = path;
    }
}
