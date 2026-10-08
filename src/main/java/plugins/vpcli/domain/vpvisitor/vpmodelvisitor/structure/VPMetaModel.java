package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.VPElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.VPElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.VPElementVisitorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VPMetaModel {
    private final IProject project;
    private final VPElementFactory createStructElement;
    private VPElementVisitorFactory visitorFactory;
    private final List<ElementType> elementTypes = new ArrayList<>();

    public VPMetaModel(IProject vpProject
        , VPElementFactory elementFactory) {

        this.project = vpProject;
        this.createStructElement = elementFactory;
//        this.visitorFactory = visitorFactory;
    }

    public void setElementTypes(List<ElementType> elementTypes) {
        this.elementTypes.addAll(elementTypes);
    }

    public void accept(VPElementVisitorFactory visitorFactory) throws IOException {
        this.visitorFactory = visitorFactory;
        List<VPElement> elements = createTopLevelElements();
        elementsAccept(elements, null);
    }

    private @NonNull List<VPElement> createTopLevelElements() {
        String[] vpModelTypes = elementTypes.stream()
            .filter( t-> t.kindIs(ElementType.Kind.MODEL_ELEMENT))
            .map( ElementType::getVPModelType)
            .toArray(String[]::new);
        var vpElements = project.toModelElementArray(vpModelTypes);

        List<VPElement> result = new ArrayList<>();
        for (var anElement : vpElements) {
            result.add(createStructElement.from(anElement));
        }
        return result;
    }

    private void elementsAccept(List<VPElement> elements
        , VPElementVisitor preLevelVisitor) throws IOException {

        int size = elements.size();
        int i = 0;

        for (var anElement : elements) {
            var visitor = visitorFactory.create(preLevelVisitor);
            visitor.setLast(i + 1 == size);
            visitor.visit(anElement);

            elementsAccept(
                anElement.getChildren(elementTypes)
                , visitor
            );

            i++;
        }
    }
}
