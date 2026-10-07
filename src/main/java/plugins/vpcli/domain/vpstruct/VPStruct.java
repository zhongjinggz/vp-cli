package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.model.IProject;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VPStruct {
    private final IProject project;
    private final VPStructElementFactory createStructElement;
    private VPVisitorFactory visitorFactory;
    private final List<ElementType> elementTypes = new ArrayList<>();

    public VPStruct(IProject vpProject
        , VPStructElementFactory elementFactory) {

        this.project = vpProject;
        this.createStructElement = elementFactory;
//        this.visitorFactory = visitorFactory;
    }

    public void setElementTypes(List<ElementType> elementTypes) {
        this.elementTypes.addAll(elementTypes);
    }

    public void accept(VPVisitorFactory visitorFactory) throws IOException {
        this.visitorFactory = visitorFactory;
        List<VPStructElement> elements = createTopLevelElements();
        elementsAccept(elements, null);
    }

    private @NonNull List<VPStructElement> createTopLevelElements() {
        String[] vpModelTypes = elementTypes.stream()
            .filter( t-> t.kindIs(ElementType.Kind.MODEL_ELEMENT))
            .map( ElementType::getVPModelType)
            .toArray(String[]::new);
        var vpElements = project.toModelElementArray(vpModelTypes);

        List<VPStructElement> result = new ArrayList<>();
        for (var anElement : vpElements) {
            result.add(createStructElement.from(anElement));
        }
        return result;
    }

    private void elementsAccept(List<VPStructElement> elements
        , VPVisitor preLevelVisitor) throws IOException {

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
