package vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;

@Singleton
public class TreeVisitorFactory implements VisitorFactory {

    @Inject
    public TreeVisitorFactory() {
    }

    @Override
    public TreeVisitor build(Visitor parentVisitor, boolean thisIsLast) {
        var preLevelTreeVisitor = (TreeVisitor) parentVisitor;

        return new TreeVisitor(
            preLevelTreeVisitor.getPrefix(),
            preLevelTreeVisitor.isLast(),
            thisIsLast
        );
    }

    @Override
    public TreeVisitor buildRoot() {
        return new TreeVisitor();
    }
}
