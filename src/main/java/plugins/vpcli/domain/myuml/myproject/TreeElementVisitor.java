package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViDiagramAsElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViModelElement;
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

    @Override
    public void visit(ViElement element) {
        String suffix = element.getType().getSuffix();
        String branch = calcBranch();
        System.out.println(this.prefix + branch + element.getName() + suffix);
    }

    @Override
    public void visit(ViDiagramAsElement viElement) throws IOException {

    }

    @Override
    public void visit(ViModelElement viElement) throws IOException {

    }

    private String calcBranch() {
        return isLast() ? "└── " : "├── ";
    }

}


