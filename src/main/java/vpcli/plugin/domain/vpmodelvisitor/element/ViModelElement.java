package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.ViElementType;

import java.util.ArrayList;
import java.util.List;

public abstract class ViModelElement extends ViElement {
    protected final IModelElement vpElement;
    private final ViElementFactory createElement;

    public ViModelElement(IModelElement vpElement,
                          ViElementFactory elementFactory
    ) {
        this.vpElement = vpElement;
        this.createElement = elementFactory;
    }

    @Override
    public String getName() {
        return vpElement.getName();
    }

    @Override
    public ViElementType getType() {
        return ViElementType.of(
            vpElement.getModelType());
    }

    @Override
    public List<ViElement> getChildren(List<ViElementType> myElementTypes) {
        List<ViElement> result = new ArrayList<>();

        var vpDiagrams = this.vpElement.toSubDiagramArray();
        if (vpDiagrams != null) {
            for (IDiagramUIModel aDiagram : vpDiagrams) {
                result.add(createElement.from(aDiagram));
            }
        }

        String[] vpModelTypes = myElementTypes.stream()
            .filter(ViElementType::isModelElement)
            .map(ViElementType::getVPModelType)
            .toArray(String[]::new);

        var vpModelElements = this.vpElement.toChildArray(vpModelTypes);
        for (IModelElement anElement : vpModelElements) {
            result.add(createElement.from(anElement));
        }
        return result;
    }

}