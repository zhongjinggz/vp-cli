package vpcli.plugin.domain.vpmodelvisitor.element.fundation;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import org.jspecify.annotations.NonNull;

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
        return getChildren(new String[]{});
    }

    @Override
    public @NonNull List<ViElement> getChildren(@NonNull String[] vpElementTypes) {
        List<ViElement> result = new ArrayList<>();

        var vpDiagrams = this.vpElement.toSubDiagramArray();
        if (vpDiagrams != null) {
            for (IDiagramUIModel aDiagram : vpDiagrams) {
                result.add(createElement.build(aDiagram));
            }
        }

        IModelElement[] vpModelElements ;

        if (vpElementTypes.length == 0) {
            vpModelElements= this.vpElement.toChildArray();
        } else {
            vpModelElements = this.vpElement.toChildArray(vpElementTypes);
        }

        for (IModelElement anElement : vpModelElements) {
            result.add(createElement.build(anElement));
        }
        return result;
    }

}