package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViProject {
    private final IProject project;
    private final ViElementFactory createElement;
    private final List<ElementType> elementTypes = new ArrayList<>();
    private ViElementVisitorFactory createVisitor;

    public ViProject(IProject vpProject
        , ViElementFactory elementFactory) {

        this.project = vpProject;
        this.createElement = elementFactory;
    }

    public void setElementTypes(List<ElementType> elementTypes) {
        this.elementTypes.addAll(elementTypes);
    }

    public void accept(ViElementVisitorFactory visitorFactory) throws IOException {
        this.createVisitor = visitorFactory;
        List<ViElement> elements = createTopLevelElements();
        ViElementVisitor rootVisitor = this.createVisitor.createRoot();
        elementsAccept(elements, rootVisitor);
    }

    private @NonNull List<ViElement> createTopLevelElements() {
        String[] vpModelTypes = elementTypes.stream()
            .filter(ElementType::isModelElement)
            .map( ElementType::getVPModelType)
            .toArray(String[]::new);
        var vpElements = project.toModelElementArray(vpModelTypes);

        List<ViElement> result = new ArrayList<>();
        for (var anElement : vpElements) {
            result.add(createElement.from(anElement));
        }
        return result;
    }

    private void elementsAccept(List<ViElement> elements
        , ViElementVisitor higherVisitor) throws IOException {

        int size = elements.size();
        int i = 0;

        for (var anElement : elements) {
            var visitor = createVisitor.under(higherVisitor);
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
