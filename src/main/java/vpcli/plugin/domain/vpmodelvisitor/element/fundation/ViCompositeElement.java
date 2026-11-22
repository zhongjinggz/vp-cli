package vpcli.plugin.domain.vpmodelvisitor.element.fundation;

import org.jspecify.annotations.NonNull;

import java.util.List;

public interface ViCompositeElement extends ViElement {

    // TODO tobe removed?
    List<ViElement> getChildren(List<ViElementType> myElementTypes);

    List<ViElement> getChildren();

    @NonNull List<ViElement> getChildren(@NonNull String[] vpElementTypes);
}
