package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.vpstruct.VPStructElement;
import plugins.vpcli.domain.vpstruct.VPVisitor;

public class TreeVisitor implements VPVisitor {
    private String prefix;

    @Override
    public void visit(VPStructElement structElement) {
        String suffix = "";
        switch (structElement.getType()) {
            case IModelElementFactory.MODEL_TYPE_PACKAGE:
                suffix = ".package";
                break;
            case IModelElementFactory.MODEL_TYPE_MODEL:
                suffix = ".model";
                break;
            default:
                throw new RuntimeException("Bug: unknow MODE_TYPE" + structElement.getType());
        }
        System.out.println(structElement.getName() + suffix);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
