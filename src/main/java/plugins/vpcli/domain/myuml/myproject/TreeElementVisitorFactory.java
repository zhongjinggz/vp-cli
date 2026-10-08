package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

public class TreeElementVisitorFactory implements ViElementVisitorFactory {

    @Override
    public ViElementVisitor create(ViElementVisitor preLevelVisitor) {
        return new TreeElementVisitor((TreeElementVisitor)preLevelVisitor) ;
    }
}
