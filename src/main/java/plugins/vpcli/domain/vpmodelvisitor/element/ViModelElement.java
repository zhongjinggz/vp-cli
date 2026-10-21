package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.util.ArrayList;
import java.util.List;

public abstract class ViModelElement extends ViElement {
    protected final IModelElement vpElement;
    private final ViElementFactory createElement;
    protected MyElementFactory myElementFactory;

    public ViModelElement(IModelElement vpElement,
                          ViElementFactory elementFactory,
                          MyElementFactory myElementFactory
    ) {
        this.vpElement = vpElement;
        this.createElement = elementFactory;
        this.myElementFactory = myElementFactory;
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
            for (IDiagramUIModel aDiagram : vpDiagrams) {
                result.add(createElement.from(aDiagram));
            }
        }

        String[] vpModelTypes = elementTypes.stream()
            .filter(ElementType::isModelElement)
            .map(ElementType::getVPModelType)
            .toArray(String[]::new);

        var vpModelElements = this.vpElement.toChildArray(vpModelTypes);
        for (IModelElement anElement : vpModelElements) {
            result.add(createElement.from(anElement));
        }
        return result;
    }

    public IModelElement getVPModelElement() {
        return this.vpElement;
    }

    public abstract MyPackage toMyElementUnder(MyElement parent);
}