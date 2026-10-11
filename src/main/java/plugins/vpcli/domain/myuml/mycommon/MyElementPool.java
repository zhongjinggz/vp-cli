package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;

import java.util.HashMap;
import java.util.Map;

public class MyElementPool {
    private Map<String, MyElement> pool = new HashMap<>();

    public MyElementPool(MyProjectFactory getMyProject) {
        var MyProject = getMyProject.fromGlobal();
        pool.put(MyProject.getId(), MyProject);
    }

    public boolean contains(String id) {
        return this.pool.containsKey(id);
    }

    public MyElement get(String id) {
        return this.pool.get(id);
    }

    public void  put(String id, MyElement element) {
        this.pool.put(id, element);
    }
}
