package plugins.vpcli.domain.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.vpmodelvisitor.ViElementType;
import plugins.vpcli.domain.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpmodelvisitor.visitor.ViElementVisitorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViProject {
    private final IProject project;
    private final ViElementFactory createElement;
    private final List<ViElementType> myElementTypes = new ArrayList<>();
    private ViElementVisitorFactory createVisitor;

    public ViProject(IProject vpProject
        , ViElementFactory elementFactory) {

        this.project = vpProject;
        this.createElement = elementFactory;
    }

    public void setElementTypes(List<ViElementType> myElementTypes) {
        this.myElementTypes.addAll(myElementTypes);
    }

    public void accept(ViElementVisitorFactory visitorCreator) throws IOException {
        this.createVisitor = visitorCreator;
        List<ViElement> elements = createTopLevelElements();
        ViElementVisitor rootVisitor = this.createVisitor.createRoot();
        elementsAccept(elements, rootVisitor);
    }

    private @NotNull List<ViElement> createTopLevelElements() {
        String[] vpModelTypes = getVpModelTypeArray();
        var vpElements = project.toModelElementArray(vpModelTypes);

        List<ViElement> result = new ArrayList<>();
        for (var anElement : vpElements) {
            result.add(createElement.from(anElement));
        }
        return result;
    }

    private String @NotNull [] getVpModelTypeArray() {
        String[] vpModelTypes = myElementTypes.stream()
            .filter(ViElementType::isModelElement)
            .map( ViElementType::getVPModelType)
            .toArray(String[]::new);
        return vpModelTypes;
    }

    private void elementsAccept(List<ViElement> elements
        , ViElementVisitor higherVisitor) throws IOException {

        int size = elements.size();
        int i = 0;

        for (var anElement : elements) {
            var visitor = createVisitor.under(higherVisitor);
            visitor.setLast(i + 1 == size);
            //visitor.visit(anElement);
            anElement.accept(visitor);

            elementsAccept(
                anElement.getChildren(myElementTypes)
                , visitor
            );

            i++;
        }
    }

}
