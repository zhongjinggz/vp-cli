package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

import java.util.HashMap;
import java.util.Map;

public class MyProject extends MyElement {
    private Map<String, MyElement> elementPool = new HashMap<>();

    public MyProject(String id, String name) {
        super(id, name);
    }
}
