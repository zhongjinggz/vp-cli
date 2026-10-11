package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.mypackage.MyModel;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.util.HashMap;
import java.util.Map;

public class MyElementFactory {
    private Map<String, MyElement> elementPool = new HashMap<>();
    public MyPackage createPackage(
        MyElement parent,
        String id,
        String name,
        String description
    ) {
        if (hasElementOf(id)) {
            return (MyPackage) this.elementPool.get(id);
        } else {
            return new MyPackage(parent, id, name, description);
        }

    }

    public MyPackage createModel(MyElement parent, String id, String name, String description) {
        if (hasElementOf(id)) {
            return (MyModel) this.elementPool.get(id);
        } else {
            return new MyModel(parent, id, name, description);
        }
    }

    private boolean hasElementOf(String id) {
        return this.elementPool.containsKey(id);
    }
}
