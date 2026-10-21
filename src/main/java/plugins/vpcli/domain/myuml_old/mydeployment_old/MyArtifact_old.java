package plugins.vpcli.domain.myuml_old.mydeployment_old;

import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

public class MyArtifact_old extends MyElement_old {

    private final boolean isInPackage;
    private final boolean isInNode;
    private String Uid;


    public MyArtifact_old(String name, boolean isInPackage, boolean isInNode) {
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
