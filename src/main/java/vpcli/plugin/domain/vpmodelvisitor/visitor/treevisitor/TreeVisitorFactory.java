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
    public Visitor under(Visitor higherVisitor) {
        return new TreeVisitor((TreeVisitor) higherVisitor) ;
    }

    //TODO use NULL Object pattern
    @Override
    public Visitor createRoot() {
        return null;
    }

    public TreeVisitor build() {
        return new TreeVisitor("", true, true);
    }
}
