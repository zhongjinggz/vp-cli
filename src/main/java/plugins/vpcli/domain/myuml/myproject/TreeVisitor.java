package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpstruct.VPStructElement;
import plugins.vpcli.domain.vpstruct.VPVisitor;

public class TreeVisitor extends VPVisitor {
    private String prefix = "";

    public TreeVisitor(TreeVisitor preLevelVisitor) {
        if (preLevelVisitor == null) {
            this.prefix = "";
        } else if (preLevelVisitor.isLast()) {
            this.prefix = preLevelVisitor.prefix + "    ";
        } else {
            this.prefix = preLevelVisitor.prefix + "│   ";
        }
    }

    @Override
    public void visit(VPStructElement structElement) {
        String suffix = structElement.getType().getSuffix();
        String branch = calcBranch();
        System.out.println(this.prefix + branch + structElement.getName() + suffix);
    }

    private String calcBranch() {
        return isLast() ? "└── " : "├── ";
    }

}


