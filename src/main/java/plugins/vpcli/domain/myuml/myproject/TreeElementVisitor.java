package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.*;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

import java.io.IOException;

public class TreeElementVisitor extends ViElementVisitor {
    private String prefix = "";

    public TreeElementVisitor(TreeElementVisitor preLevelVisitor) {
        if (preLevelVisitor == null) {
            this.prefix = "";
        } else if (preLevelVisitor.isLast()) {
            this.prefix = preLevelVisitor.prefix + "    ";
        } else {
            this.prefix = preLevelVisitor.prefix + "│   ";
        }
    }

//    @Override
//    public void visit(ViElement element) {
//        String suffix = element.getType().getSuffix();
//        String branch = calcBranch();
//        System.out.println(this.prefix + branch + element.getName() + suffix);
//    }

    @Override
    public void visit(ViDiagramAsElement element) throws IOException {
        printBranch(element.getName(), ".diagram");

    }

    @Override
    public void visit(ViPackage element) throws IOException {
        printBranch(element.getName(), ".package");
    }

    @Override
    public void visit(ViModel element) throws IOException {
        printBranch(element.getName(), ".model");

    }

    @Override
    public void visit(ViClass element) throws IOException {
        printBranch(element.getName(), ".class");
    }

    @Override
    public void visit(ViUseCase element) throws IOException {
        printBranch(element.getName(), ".usecase");
    }

    private void printBranch(String element, String suffix) {
        String branch = isLast() ? "└── " : "├── ";
        System.out.println(this.prefix + branch + element + suffix);
    }

}


