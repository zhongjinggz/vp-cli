package vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure;

import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.vpmodelvisitor.element.ViCompositeElement;
import vpcli.plugin.domain.vpmodelvisitor.element.ViElement;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;

import java.util.List;

import static com.vp.plugin.model.factory.IModelElementFactory.*;

public class Traveller {
    private final VisitorFactory visitorFactory;

    public Traveller(VisitorFactory visitorFactory) {
        this.visitorFactory = visitorFactory;
    }

    protected String @NonNull [] getVpElementTypes() {
        return new String[]{
            MODEL_TYPE_MODEL,
            MODEL_TYPE_PACKAGE,
            MODEL_TYPE_CLASS,
            MODEL_TYPE_USE_CASE
        };
    }

    public void travel(ViProject viProject) {
        Visitor visitor = visitorFactory.buildRoot();
        travelElement(viProject, visitor);
    }

    private void travelElement(ViElement element, Visitor visitor) {

        String[] vpElementTypes = getVpElementTypes();

        element.accept(visitor);

        if (element instanceof ViCompositeElement) {

            List<ViElement> subElements = ((ViCompositeElement) element).getChildren(vpElementTypes);
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
