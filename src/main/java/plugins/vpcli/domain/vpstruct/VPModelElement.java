package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import java.util.ArrayList;
import java.util.List;

import static plugins.vpcli.domain.vpstruct.ElementType.Kind.MODEL_ELEMENT;

public class VPModelElement extends VPStructElement {
    private final IModelElement vpElement;
    private final VPStructElementFactory createElement;

    public VPModelElement(IModelElement vpElement, VPStructElementFactory elementFactory) {
        this.vpElement = vpElement;
        this.createElement = elementFactory;
    }
    public String getName() {
        return vpElement.getName();
    }
    
    public ElementType getType() {
        return ElementType.of(MODEL_ELEMENT
            ,vpElement.getModelType());
    }

    @Override
    public List<VPStructElement> getChildren(List<ElementType> elementTypes) {
        List<VPStructElement> result = new ArrayList<>();

        var vpDiagrams = this.vpElement.toSubDiagramArray();
        if (vpDiagrams != null) {
            for (IDiagramUIModel aDiagram: vpDiagrams) {
                result.add(createElement.from(aDiagram));
            }
        }

        String[] vpModelTypes = elementTypes.stream()
            .filter(t -> t.kindIs(MODEL_ELEMENT))
            .map(t-> t.getVPModelType())
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