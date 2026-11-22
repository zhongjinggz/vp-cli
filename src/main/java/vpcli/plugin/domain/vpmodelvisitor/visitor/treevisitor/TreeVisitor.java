package vpcli.plugin.domain.vpmodelvisitor.visitor.treevisitor;

import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViClass;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViNary;
import vpcli.plugin.domain.vpmodelvisitor.element.viclassifier.ViUseCase;
import vpcli.plugin.domain.vpmodelvisitor.element.vidiagram.ViDiagram;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViModel;
import vpcli.plugin.domain.vpmodelvisitor.element.vipackage.ViPackage;
import vpcli.plugin.domain.vpmodelvisitor.element.viproject.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.visitor.Visitor;

import static vpcli.plugin.domain.vpmodel.ModelElements.hasStereotype;

public class TreeVisitor extends Visitor {

    private final String prefix;
    private final boolean thisIsLast;

    TreeVisitor(String parentPrefix,
                       boolean parentIsLast,
                       boolean thisIsLast
    ) {
        this.thisIsLast = thisIsLast;

        if (parentIsLast) {
            this.prefix = parentPrefix + "    ";
        } else {
            this.prefix = parentPrefix + "│   ";
        }
    }

    // for root visitor only
    TreeVisitor() {
        prefix = "";
        thisIsLast = true;
    }

    @Override
    public void visit(ViDiagram element) {
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
        String branch = this.thisIsLast ? "└── " : "├── ";
        System.out.println(this.prefix + branch + element + suffix);
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isLast() {
        return thisIsLast;
    }
}


