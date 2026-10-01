package plugins.vpcli.domain.myuml.mydeployment;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyArtifact extends MyElement {

    private final boolean isInPackage;
    private final boolean isInNode;
    private String Uid;


    public MyArtifact(String name, boolean isInPackage, boolean isInNode) {
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
