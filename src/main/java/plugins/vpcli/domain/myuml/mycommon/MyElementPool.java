package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;

import java.util.HashMap;
import java.util.Map;

public class MyElementPool {
    private final MyProject myProject;
    private final Map<String, MyElement> pool = new HashMap<>();

    public MyElementPool(MyProjectFactory getMyProject) {
        this.myProject = getMyProject.fromGlobal();
        pool.put(this.myProject.getId(), this.myProject);
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

    public MyElement getMyProject() {
        return myProject;
    }
}
