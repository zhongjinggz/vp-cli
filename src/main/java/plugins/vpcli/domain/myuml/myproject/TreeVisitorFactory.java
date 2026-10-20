package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
