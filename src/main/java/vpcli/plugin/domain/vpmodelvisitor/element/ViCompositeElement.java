package vpcli.plugin.domain.vpmodelvisitor.element;

import vpcli.plugin.domain.vpmodelvisitor.ViElementType;

import java.util.List;

public interface ViCompositeElement extends ViElement {

    List<ViElement> getChildren(List<ViElementType> myElementTypes);

    List<ViElement> getChildren();
}
