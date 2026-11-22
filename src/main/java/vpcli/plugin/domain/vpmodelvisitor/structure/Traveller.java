package vpcli.plugin.domain.vpmodelvisitor.structure;

import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViCompositeElement;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElement;
import vpcli.plugin.domain.vpmodelvisitor.element.viproject.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactoryWithFileSystem;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactoryWithoutFileSystem;
import vpcli.plugin.util.BugException;

import java.io.File;
import java.util.List;

public class Traveller {
    private final VisitorFactory visitorFactory;
    private final String[] vpElementTypes;

    public Traveller(VisitorFactory visitorFactory, String[] vpElementTypes) {
        this.visitorFactory = visitorFactory;
        this.vpElementTypes = vpElementTypes;
    }

    public void travel(ViProject viProject) {
        Visitor visitor;
        if (visitorFactory instanceof VisitorFactoryWithoutFileSystem) {
            visitor = ((VisitorFactoryWithoutFileSystem) visitorFactory).buildRoot();
        } else {
            throw new BugException("Bug: cannot conver to VisitorFactoryWithoutFileSystem!");
        }
        travelElement(viProject, visitor);
    }

    public void travel(ViProject viProject, File rootPath) {
        Visitor visitor;
        if (visitorFactory instanceof VisitorFactoryWithFileSystem) {
            visitor = ((VisitorFactoryWithFileSystem) visitorFactory).buildRoot(rootPath);
        } else {
            throw new BugException("Bug: cannot conver to VisitorFactoryWithFileSystem!");
        }
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
