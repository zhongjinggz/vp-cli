package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;

import java.util.HashMap;
import java.util.Map;

public abstract class MyElementFactory {
    private Map<String, MyElement> elementPool = new HashMap<>();


    // TOBE abstract
    public abstract MyElement reallyCreate(MyElement parent, IModelElement vpElement);

    public MyElement create(MyElement parent, IModelElement vpElement) {
        if (hasMyElementOf(vpElement.getId())) {
            return MyElementOf(vpElement.getId());
        } else {
            return reallyCreate(parent, vpElement);
        }
    }

    public MyElement create(IModelElement vpElement) {
        var vpParentElement = vpElement.getParent();
        var parentId = vpParentElement.getId();

        MyElement myParentElement;

        if (hasMyElementOf(parentId)) {
            myParentElement = MyElementOf(parentId);
        } else {
            myParentElement = create(vpParentElement);
        }
        return create(myParentElement, vpElement);
    }

    private boolean hasMyElementOf(String id) {
        return this.elementPool.containsKey(id);
    }

    private MyElement MyElementOf(String vpElement) {
        return this.elementPool.get(vpElement);
    }
}
