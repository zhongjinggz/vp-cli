package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;

public class MyProject extends MyElement {

    public MyProject(String id, String name) {
        super(id, name, MyElementType.PROJECT);
    }
}
