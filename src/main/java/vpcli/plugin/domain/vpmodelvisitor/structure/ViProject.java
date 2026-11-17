package vpcli.plugin.domain.vpmodelvisitor.structure;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
//import org.jetbrains.annotations.NotNull;
import vpcli.plugin.domain.vpmodelvisitor.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.element.ViCompositeElement;
import vpcli.plugin.domain.vpmodelvisitor.element.ViElement;
import vpcli.plugin.domain.vpmodelvisitor.element.ViElementFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViProject implements ViCompositeElement {
    private final IProject vpProject;
    private final ViElementFactory viElementFactory;
    private final List<ViElementType> viElementTypes = new ArrayList<>();
    private VisitorFactory visitorFactory;

    public ViProject(IProject vpProject
        , ViElementFactory elementFactory) {

        this.vpProject = vpProject;
        this.viElementFactory = elementFactory;
    }

    public void setElementTypes(List<ViElementType> myElementTypes) {
        this.viElementTypes.addAll(myElementTypes);
    }

    public void accept(VisitorFactory visitorFactory) throws IOException {
        this.visitorFactory = visitorFactory;
        List<ViElement> elements = createTopLevelElements();
        Visitor rootVisitor = this.visitorFactory.buildRoot();
        elementsAccept(elements, rootVisitor);
    }

    private List<ViElement> createTopLevelElements() {
        String[] vpModelTypes = getVpModelTypeArray();
        var vpElements = vpProject.toModelElementArray(vpModelTypes);

        List<ViElement> result = new ArrayList<>();
        for (var anElement : vpElements) {
            result.add(viElementFactory.build(anElement));
        }
        return result;
    }

    @Override
    public List<ViElement> getChildren(String[] elementTypes) {

        IModelElement[] vpElements;
        if (elementTypes.length == 0) {
            vpElements = vpProject.toModelElementArray();
        } else {
            vpElements = vpProject.toModelElementArray(elementTypes);
        }

        List<ViElement> result = new ArrayList<>();
        for (var anElement : vpElements) {
            result.add(viElementFactory.build(anElement));
        }
        return result;
    }

    private String[] getVpModelTypeArray() {
        return viElementTypes.stream()
            .filter(ViElementType::isModelElement)
            .map(ViElementType::getVPModelType)
            .toArray(String[]::new);
    }

    private void elementsAccept(List<ViElement> elements
        , Visitor higherVisitor) {

        int size = elements.size();
        int i = 0;

        for (var anElement : elements) {
            var visitor = visitorFactory.build(higherVisitor);
            visitor.setLast(i + 1 == size);
            anElement.accept(visitor);

            if (anElement instanceof ViCompositeElement) {
                var anCompositeElement = (ViCompositeElement) anElement;
                elementsAccept(
                    anCompositeElement.getChildren(viElementTypes)
                    , visitor
                );

            }

            i++;
        }
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    // TODO ToBe removed?
    public List<ViElement> getChildren(List<ViElementType> myElementTypes) {
        return List.of();
    }

    @Override
    public List<ViElement> getChildren() {
        return getChildren(new String[]{});
    }


    @Override
    public String getName() {
        return vpProject.getName();
    }

    @Override
    public ViElementType getType() {
        return ViElementType.PROJECT;
    }
}
