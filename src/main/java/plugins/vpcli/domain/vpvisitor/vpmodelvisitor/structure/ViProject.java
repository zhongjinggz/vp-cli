package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure;

import com.vp.plugin.model.IProject;
import org.checkerframework.checker.nullness.qual.NonNull;
import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViProject {
    private final IProject project;
    private final ViElementFactory createStructElement;
    private final List<ElementType> elementTypes = new ArrayList<>();
    private final MyProject myProject ;
    private ViElementVisitorFactory visitorFactory;

    public ViProject(IProject vpProject
        , ViElementFactory elementFactory) {

        this.project = vpProject;
        this.createStructElement = elementFactory;
        this.myProject = null;
    }

    public ViProject(IProject vpProject, MyProject myProject, ViElementFactory viElementFactory) {
        this.project = vpProject;
        this.createStructElement = viElementFactory;
        this.myProject = myProject;
    }

    public void setElementTypes(List<ElementType> elementTypes) {
        this.elementTypes.addAll(elementTypes);
    }

    public void accept(ViElementVisitorFactory visitorFactory) throws IOException {
        this.visitorFactory = visitorFactory;
        List<ViElement> elements = createTopLevelElements();
        ViElementVisitor rootVisitor = this.visitorFactory.createRoot();
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
            result.add(createStructElement.from(anElement));
        }
        return result;
    }

    private void elementsAccept(List<ViElement> elements
        , ViElementVisitor preLevelVisitor) throws IOException {

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
