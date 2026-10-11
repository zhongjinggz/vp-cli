package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;

public abstract class MyElementFactory {
//    private Map<String, MyElement> elementPool = new HashMap<>();

    private final MyElementPool elementPool;

    public MyElementFactory(MyElementPool myElementPool) {
        this.elementPool = myElementPool;
    }

    // TOBE abstract
    public abstract MyElement reallyCreate(MyElement parent, IModelElement vpElement);

    public MyElement create(MyElement parent, IModelElement vpElement) {
        if (elementPool.contains(vpElement.getId())) {
            return elementPool.get(vpElement.getId());
        } else {
            return reallyCreate(parent, vpElement);
        }
    }

    public MyElement create(IModelElement vpElement) {
        var vpParentElement = vpElement.getParent();
        var parentId = vpParentElement.getId();

        MyElement myParentElement;

        if (elementPool.contains(parentId)) {
            myParentElement = elementPool.get(parentId);
        } else {
            myParentElement = create(vpParentElement);
        }
        return create(myParentElement, vpElement);
    }

}
