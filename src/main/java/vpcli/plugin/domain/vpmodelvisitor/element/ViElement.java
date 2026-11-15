package vpcli.plugin.domain.vpmodelvisitor.element;

import vpcli.plugin.domain.vpmodelvisitor.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import java.io.IOException;

public interface ViElement {
    void accept(Visitor visitor) throws IOException;

    String getName();

    ViElementType getType();
}
