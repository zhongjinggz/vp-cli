package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

public class TreeElementVisitor extends ViElementVisitor {
    private final String prefix ;

    public TreeElementVisitor(TreeElementVisitor preLevelVisitor) {
        super(null);
        if (preLevelVisitor == null) {
            this.prefix = "";
        } else if (preLevelVisitor.isLast()) {
            this.prefix = preLevelVisitor.prefix + "    ";
        } else {
            this.prefix = preLevelVisitor.prefix + "│   ";
        }
    }

    @Override
    public void visit(ViDiagramAsElement element) {
        printBranch(element.getName(), ".diagram");

    }

    @Override
    public void visit(ViPackage element) {
        printBranch(element.getName(), ".package");
    }

    @Override
    public void visit(ViModel element) {
        printBranch(element.getName(), ".model");

    }

    @Override
    public void visit(ViClass element) {
        printBranch(element.getName(), ".class");
    }

    @Override
    public void visit(ViUseCase element) {
        printBranch(element.getName(), ".usecase");
    }

    private void printBranch(String element, String suffix) {
        String branch = isLast() ? "└── " : "├── ";
        System.out.println(this.prefix + branch + element + suffix);
    }

}


