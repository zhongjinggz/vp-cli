package plugins.vpcli.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import plugins.vpcli.domain.myuml.myproject.MyProject;

import java.io.File;

// it is an implementation of "Null Object Pattern"
public class RootExportDiagramVisitor extends ExportDiagramVisitor {
    public RootExportDiagramVisitor(File path, MyProject myProject) {
        super(path, myProject);
    }
}
