package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyProject extends MyElement {

    public MyProject(String id, String name) {
        super(id, name, ElementType.PROJECT);
    }
}
