package vpcli.plugin.domain.vpmodelvisitor.element.fundation;

import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

public interface ViElement {
    void accept(Visitor visitor) ;

    String getName();

    ViElementType getType();
}
