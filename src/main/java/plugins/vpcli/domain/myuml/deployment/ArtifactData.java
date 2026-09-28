package plugins.vpcli.domain.myuml.deployment;

import plugins.vpcli.domain.myuml.mystatemachine.BaseWithSemanticsData;

public class ArtifactData extends BaseWithSemanticsData {

    private final boolean isInPackage;
    private final boolean isInNode;
    private String Uid;


    public ArtifactData(String name, boolean isInPackage, boolean isInNode) {
        super(name);
        this.isInPackage = isInPackage;
        this.isInNode = isInNode;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public boolean isInPackage() {
        return isInPackage;
    }

    public boolean isInNode() {
        return isInNode;
    }

}
