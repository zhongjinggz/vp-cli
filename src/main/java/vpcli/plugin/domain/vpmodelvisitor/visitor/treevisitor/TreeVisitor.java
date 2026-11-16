package vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor;

import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.vpmodelvisitor.element.*;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import java.util.List;

import static com.vp.plugin.model.factory.IModelElementFactory.*;
import static vpcli.plugin.domain.vpmodel.ModelElements.hasStereotype;

public class TreeVisitor extends Visitor {
    private final static String[] vpElementTypes = new String[]{
        MODEL_TYPE_MODEL,
        MODEL_TYPE_PACKAGE,
        MODEL_TYPE_CLASS,
        MODEL_TYPE_USE_CASE
    };

    private final String prefix;
    private boolean thisLevelIsLast;

    public TreeVisitor(TreeVisitor preLevelVisitor) {
        super(null);
        if (preLevelVisitor == null) {
            this.prefix = "";
        } else if (preLevelVisitor.isLast()) {
            this.prefix = preLevelVisitor.prefix + "    ";
        } else {
            this.prefix = preLevelVisitor.prefix + "│   ";
        }
    }

    public TreeVisitor(String preLevelPrefix,
                       boolean preLevelIsLast,
                       boolean thisLevelIsLast
    ) {
        super(null);
        this.thisLevelIsLast = thisLevelIsLast;

        if (preLevelIsLast) {
            this.prefix = preLevelPrefix + "    ";
        } else {
            this.prefix = preLevelPrefix + "│   ";
        }
    }

    // for root visitor only
    TreeVisitor() {
        super(null);
        prefix = "";
        thisLevelIsLast = true;
    }

    public static TreeVisitor root() {
        return new TreeVisitor();
    }

    public static TreeVisitor of(
        String preLevelPrefix,
        boolean preLevelIsLast,
        boolean thisLevelIsLast
    ) {
        return new TreeVisitor(preLevelPrefix, preLevelIsLast, thisLevelIsLast);
    }

    @Override
    public void visit(ViDiagramAsElement element) {
        printBranch(element.getName(), ".diagram");

    }

    @Override
    public void visit(ViProject element) {

        List<ViElement> elements = element.getChildren(vpElementTypes);

        int i = 0;
        for (var anElement : elements) {
            boolean nextLevelIsLast = (i + 1 == elements.size());
            var visitor = buildTreeVisitor(nextLevelIsLast);
            anElement.accept(visitor);
            i++;
        }
    }

    // Mock framework can spy the TreeVisitor here
    @NonNull TreeVisitor buildTreeVisitor(boolean nextLevelIsLast) {
        return TreeVisitor.of(this.prefix, thisLevelIsLast, nextLevelIsLast);
    }

    @Override
    public void visit(ViPackage element) {
        printBranch(element.getName(), "");

        List<ViElement> elements = element.getChildren(vpElementTypes);

        int i = 0;
        for (var anElement : elements) {
            boolean nextLevelIsLast = (i + 1 == elements.size());
            var visitor = buildTreeVisitor(nextLevelIsLast);
            anElement.accept(visitor);
            i++;
        }
    }

    @Override
    public void visit(ViModel element) {
        printBranch(element.getName(), "");

        List<ViElement> elements = element.getChildren(vpElementTypes);

        int i = 0;
        for (var anElement : elements) {
            boolean nextLevelIsLast = (i + 1 == elements.size());
            var visitor = buildTreeVisitor(nextLevelIsLast);
            anElement.accept(visitor);
            i++;
        }

    }

    @Override
    public void visit(ViNary element) {
        printBranch(element.getName(), ".nary");
    }

    @Override
    public void visit(ViClass element) {
        String suffix;
        if (hasStereotype(element.getVPElement(), "Interface")) {
            suffix = ".interface";
        } else if (hasStereotype(element.getVPElement(), "enumeration")) {
            suffix = ".enumeration";
        } else {
            suffix = ".class";
        }
        printBranch(element.getName(), suffix);
    }

    @Override
    public void visit(ViUseCase element) {
        printBranch(element.getName(), ".usecase");
    }


    private void printBranch(String element, String suffix) {
        String branch = this.thisLevelIsLast ? "└── " : "├── ";
        System.out.println(this.prefix + branch + element + suffix);
    }

}


