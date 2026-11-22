package vpcli.plugin.domain.vpmodelvisitor.element.viproject;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElementType;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViCompositeElement;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElement;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElementFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class ViProject implements ViCompositeElement {
    private final IProject vpProject;
    private final ViElementFactory viElementFactory;

    public ViProject(IProject vpProject
        , ViElementFactory elementFactory) {

        this.vpProject = vpProject;
        this.viElementFactory = elementFactory;
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
