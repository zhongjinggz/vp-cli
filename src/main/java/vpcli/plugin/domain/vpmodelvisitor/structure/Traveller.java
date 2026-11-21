package vpcli.plugin.domain.vpmodelvisitor.structure;

import vpcli.plugin.domain.vpmodelvisitor.element.ViCompositeElement;
import vpcli.plugin.domain.vpmodelvisitor.element.ViElement;
import vpcli.plugin.domain.vpmodelvisitor.element.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;

import java.util.List;

public class Traveller {
    private final VisitorFactory visitorFactory;
    private final String[] vpElementTypes;

    public Traveller(VisitorFactory visitorFactory, String[] vpElementTypes) {
        this.visitorFactory = visitorFactory;
        this.vpElementTypes = vpElementTypes;
    }

    public void travel(ViProject viProject) {
        Visitor visitor = visitorFactory.buildRoot();
        travelElement(viProject, visitor);
    }

    private void travelElement(ViElement element, Visitor visitor) {
        element.accept(visitor);

        if (element instanceof ViCompositeElement) {

            List<ViElement> subElements = ((ViCompositeElement) element).getChildren(this.vpElementTypes);
            int i = 0;

            for (var subElement : subElements) {
                boolean isLast = (i + 1 == subElements.size());

                var subVisitor = visitorFactory.build(visitor, isLast);
                travelElement(subElement, subVisitor);
                i++;
            }
        }
    }
}
