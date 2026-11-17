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
    public TreeVisitor build(Visitor preLevelVisitor, boolean thisLevelIsLast) {
        var preLevelTreeVisitor = (TreeVisitor) preLevelVisitor;

        return new TreeVisitor(
            preLevelTreeVisitor.getPrefix(),
            preLevelTreeVisitor.isLast(),
            thisLevelIsLast
        );
    }

    @Override
    // don't use it, will be replaced by above build()
    public Visitor build(Visitor higherVisitor) {
        return build(higherVisitor, true);
    }

    public TreeVisitor buildRoot() {
        return new TreeVisitor();
    }
}
