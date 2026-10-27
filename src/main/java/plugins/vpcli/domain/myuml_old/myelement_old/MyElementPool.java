package plugins.vpcli.domain.myuml_old.myelement_old;

import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;

import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyElementPool {
    private final MyProject myProject;
    private final Map<String, MyElement_old2> pool = new HashMap<>();

    @Inject
    public MyElementPool(MyProjectFactory getMyProject) {
        this.myProject = getMyProject.fromGlobal();
        pool.put(this.myProject.getId(), this.myProject);
    }

    public boolean contains(String id) {
        return this.pool.containsKey(id);
    }

    public MyElement_old2 get(String id) {
        return this.pool.get(id);
    }

    public void  put(String id, MyElement_old2 element) {
        this.pool.put(id, element);
    }

    public MyElement_old2 getMyProject() {
        return myProject;
    }
}
