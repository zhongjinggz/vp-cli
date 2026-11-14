package vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.vpmodelvisitor.visitor.ViElementVisitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;

@Singleton
public class TreeVisitorFactory implements VisitorFactory {

    @Inject
    public TreeVisitorFactory() {
    }

    @Override
    public ViElementVisitor under(ViElementVisitor higherVisitor) {
        return new TreeElementVisitor((TreeElementVisitor) higherVisitor) ;
    }

    //TODO use NULL Object pattern
    @Override
    public ViElementVisitor createRoot() {
        return null;
    }
}
