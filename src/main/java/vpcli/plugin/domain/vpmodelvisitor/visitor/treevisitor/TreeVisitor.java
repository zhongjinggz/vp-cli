package vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor;

import vpcli.plugin.domain.vpmodelvisitor.element.*;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import static vpcli.plugin.domain.vpmodel.ModelElements.hasStereotype;

public class TreeVisitor extends Visitor {

    private final String prefix;
    private final boolean thisLevelIsLast;

    TreeVisitor(String preLevelPrefix,
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

    @Override
    public void visit(ViDiagramAsElement element) {
        printBranch(element.getName(), ".diagram");

    }

    @Override
    public void visit(ViProject element) {
        printBranch(element.getName(), "");
    }

    @Override
    public void visit(ViPackage element) {
        printBranch(element.getName(), "");
    }

    @Override
    public void visit(ViModel element) {
        printBranch(element.getName(), "");
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

    public String getPrefix() {
        return prefix;
    }

    public boolean isLast() {
        return thisLevelIsLast;
    }
}


