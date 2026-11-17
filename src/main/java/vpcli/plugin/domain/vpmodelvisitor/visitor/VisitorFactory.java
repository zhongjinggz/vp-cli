package vpcli.plugin.domain.vpmodelvisitor.visitor;

public interface VisitorFactory {
    Visitor build(Visitor preLevelVisitor, boolean thisLevelIsLast);

    Visitor buildRoot();
}
