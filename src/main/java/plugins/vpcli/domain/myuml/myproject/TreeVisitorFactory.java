package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpstruct.VPVisitor;
import plugins.vpcli.domain.vpstruct.VPVisitorFactory;

public class TreeVisitorFactory implements VPVisitorFactory {

    @Override
    public VPVisitor create(VPVisitor preLevelVisitor) {
        return new TreeVisitor() ;
    }
}
