package vpcli.plugin.domain.vpmodelvisitor.visitor;

import java.io.File;

public interface VisitorFactoryWithFileSystem extends VisitorFactory{

    Visitor buildRoot(File rootPath);
}
