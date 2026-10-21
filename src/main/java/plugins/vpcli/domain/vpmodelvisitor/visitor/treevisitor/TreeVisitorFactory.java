package plugins.vpcli.domain.vpmodelvisitor.visitor.treevisitor;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitorFactory;

@Singleton
public class TreeVisitorFactory implements ViElementVisitorFactory {

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
