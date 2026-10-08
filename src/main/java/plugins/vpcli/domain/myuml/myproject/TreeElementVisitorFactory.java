package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitorFactory;

public class TreeElementVisitorFactory implements VPElementVisitorFactory {

    @Override
    public VPElementVisitor create(VPElementVisitor preLevelVisitor) {
        return new TreeElementVisitor((TreeElementVisitor)preLevelVisitor) ;
    }
}
