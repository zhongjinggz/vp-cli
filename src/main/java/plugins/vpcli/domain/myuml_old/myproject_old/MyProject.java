package plugins.vpcli.domain.myuml_old.myproject_old;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;

public class MyProject extends MyElement_old2 {

    public MyProject(String id, String name) {
        super(id, name, MyElementType.PROJECT);
    }
}
