package vpcli.plugin.domain.vpmodelvisitor.visitor;

import vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor.TreeVisitor;

public interface VisitorFactory {
    Visitor build(Visitor preLevelVisitor, boolean thisLevelIsLast);

    Visitor build(Visitor higherVisitor);

    Visitor buildRoot();
}
