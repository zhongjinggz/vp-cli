package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.vpmodelvisitor.ViElementType;

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

    public IModelElement getVPModelElement() {
        return this.vpElement;
    }

    public abstract MyPackage toMyElementUnder(MyElement parent);
}