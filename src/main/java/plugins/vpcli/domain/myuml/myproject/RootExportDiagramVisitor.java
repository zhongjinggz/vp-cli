package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.File;

// it is an implementation of "Null Object Pattern"
public class RootExportDiagramVisitor extends ExportDiagramVisitor {
    public RootExportDiagramVisitor(File path) {
        super(path);
    }
}
