package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

public class TreeVisitorCreator implements ViElementVisitorFactory {

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
