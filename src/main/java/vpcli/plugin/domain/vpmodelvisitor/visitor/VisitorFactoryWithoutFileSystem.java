package vpcli.plugin.domain.vpmodelvisitor.visitor;

public interface VisitorFactoryWithoutFileSystem extends VisitorFactory{

    Visitor buildRoot();
}
