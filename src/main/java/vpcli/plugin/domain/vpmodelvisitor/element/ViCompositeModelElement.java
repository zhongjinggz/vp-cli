package vpcli.plugin.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import vpcli.plugin.domain.vpmodelvisitor.ViElementType;

import java.util.ArrayList;
import java.util.List;

public abstract class ViCompositeModelElement extends ViModelElement implements ViCompositeElement {
    private final ViElementFactory createElement;

    public ViCompositeModelElement(IModelElement vpElement,
                                   ViElementFactory elementFactory
    ) {
        super(vpElement);
        this.createElement = elementFactory;
    }

    @Override
    public List<ViElement> getChildren(List<ViElementType> myElementTypes) {
        List<ViElement> result = new ArrayList<>();

        var vpDiagrams = this.vpElement.toSubDiagramArray();
        if (vpDiagrams != null) {
            for (IDiagramUIModel aDiagram : vpDiagrams) {
                result.add(createElement.build(aDiagram));
            }
        }

        String[] vpModelTypes = myElementTypes.stream()
            .filter(ViElementType::isModelElement)
            .map(ViElementType::getVPModelType)
            .toArray(String[]::new);

        var vpModelElements = this.vpElement.toChildArray(vpModelTypes);
        for (IModelElement anElement : vpModelElements) {
            result.add(createElement.build(anElement));
        }
        return result;
    }

    @Override
    public List<ViElement> getChildren() {
        List<ViElement> result = new ArrayList<>();

        var vpDiagrams = this.vpElement.toSubDiagramArray();
        if (vpDiagrams != null) {
            for (IDiagramUIModel aDiagram : vpDiagrams) {
                result.add(createElement.build(aDiagram));
            }
        }

        var vpModelElements = this.vpElement.toChildArray();
        for (IModelElement anElement : vpModelElements) {
            result.add(createElement.build(anElement));
        }
        return result;
    }

}