package vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor;

import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.vpmodelvisitor.element.*;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import java.util.List;

public class TreeVisitor extends Visitor {
    private final String prefix;
    private boolean highLevelIsLast;
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

    public TreeVisitor(@NonNull String highLevelPrefix, boolean highLevelIsLast, boolean thisLevelIsLast) {
        super(null);
        this.highLevelIsLast = highLevelIsLast;
        this.thisLevelIsLast = thisLevelIsLast;

        if (highLevelIsLast) {
            this.prefix = highLevelPrefix + "    ";
        } else {
            this.prefix = highLevelPrefix + "│   ";
        }
    }

    public TreeVisitor() {
        super(null);
        prefix = "";
        thisLevelIsLast = true;
        highLevelIsLast = true;
    }

    public static TreeVisitor root() {
        return new TreeVisitor();
    }

    public static TreeVisitor of(@NonNull String highLevelPrefix, boolean highLevelIsLast, boolean thisLevelIsLast){
        return new TreeVisitor(highLevelPrefix, highLevelIsLast, thisLevelIsLast);
    }

    @Override
    public void visit(ViDiagramAsElement element) {
        printBranch(element.getName(), ".diagram");

    }

    @Override
    public void visit(ViPackage element) {
        printBranch(element.getName(), "");

        List<ViElement> elements = element.getChildren();

        int i = 0;
        for (var anElement : elements) {
            boolean nextLevelIsLast = (i + 1 == elements.size());
            var visitor = TreeVisitor.of(this.prefix, thisLevelIsLast, nextLevelIsLast);
            anElement.accept(visitor);
            i++;
        }
    }

    @Override
    public void visit(ViModel element) {
        printBranch(element.getName(), "");

        List<ViElement> elements = element.getChildren();

        int i = 0;
        for (var anElement : elements) {
            boolean nextLevelIsLast = (i + 1 == elements.size());
            var visitor = TreeVisitor.of(this.prefix, thisLevelIsLast, nextLevelIsLast);
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
        printBranch(element.getName(), ".class");
    }

    @Override
    public void visit(ViUseCase element) {
        printBranch(element.getName(), ".usecase");
    }

    @Override
    public void visit(ViProject viProject) {

        List<ViElement> elements = viProject.getChildren();

        int i = 0;
        for (var anElement : elements) {
            boolean nextLevelIsLast = (i + 1 == elements.size());
            var visitor = TreeVisitor.of(this.prefix, thisLevelIsLast, nextLevelIsLast);
            anElement.accept(visitor);
            i++;
        }
    }


    private void printBranch(String element, String suffix) {
        String branch = this.thisLevelIsLast ? "└── " : "├── ";
        System.out.println(this.prefix + branch + element + suffix);
    }

}


