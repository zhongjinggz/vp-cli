package plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import java.io.File;

// it is an implementation of "Null Object Pattern"
public class RootExportDiagramVisitor extends ExportDiagramVisitor {
    public RootExportDiagramVisitor(File path) {
        super(path);
    }
}
