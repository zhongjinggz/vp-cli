package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElement;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;

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
    public void visit(ViElement structElement) {
        String suffix = structElement.getType().getSuffix();
        String branch = calcBranch();
        System.out.println(this.prefix + branch + structElement.getName() + suffix);
    }

    private String calcBranch() {
        return isLast() ? "└── " : "├── ";
    }

}


