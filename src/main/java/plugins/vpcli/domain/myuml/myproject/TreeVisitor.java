package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.factory.IModelElementFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
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
        String suffix = calcSuffix(structElement);
        String branch = calcBranch();
        System.out.println(this.prefix + branch + structElement.getName() + suffix);
    }

    private String calcBranch() {
        return isLast() ? "└── " : "├── ";
    }

    private @NonNull String calcSuffix(VPStructElement structElement) {
        String suffix = "";
        switch (structElement.getType()) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                suffix = ".package";
                break;
            case IModelElementFactory.MODEL_TYPE_MODEL:
                suffix = ".model";
                break;
            case IModelElementFactory.MODEL_TYPE_CLASS:
                suffix = ".class";
                break;
            default:
                throw new RuntimeException("Bug: unknow MODE_TYPE" + structElement.getType());
        }
        return suffix;
    }
}


