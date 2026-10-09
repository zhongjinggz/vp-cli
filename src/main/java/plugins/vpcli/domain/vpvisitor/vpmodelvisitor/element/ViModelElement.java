package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.ElementType;

import java.util.ArrayList;
import java.util.List;

public class ViModelElement extends ViElement {
    private final IModelElement vpElement;
    private final ViElementFactory createElement;

    public ViModelElement(IModelElement vpElement, ViElementFactory elementFactory) {
        this.vpElement = vpElement;
        this.createElement = elementFactory;
    }
    public String getName() {
        return vpElement.getName();
    }
    
    public ElementType getType() {
        return ElementType.of(
            vpElement.getModelType());
    }

    @Override
    public List<ViElement> getChildren(List<ElementType> elementTypes) {
        List<ViElement> result = new ArrayList<>();

        var vpDiagrams = this.vpElement.toSubDiagramArray();
        if (vpDiagrams != null) {
            for (IDiagramUIModel aDiagram: vpDiagrams) {
                result.add(createElement.from(aDiagram));
            }
        }

        String[] vpModelTypes = elementTypes.stream()
            .filter(ElementType::isModelElement)
            .map(ElementType::getVPModelType)
            .toArray(String[]::new);

        var vpModelElements = this.vpElement.toChildArray(vpModelTypes);
        for (IModelElement anElement: vpModelElements) {
           result.add(createElement.from(anElement));
        }
        return result;
    }

    public IModelElement getVPModelElement() {
        return this.vpElement;
    }
}