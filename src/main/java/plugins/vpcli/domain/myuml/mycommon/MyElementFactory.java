package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;

import java.util.HashMap;
import java.util.Map;

public abstract class MyElementFactory {
    private Map<String, MyElement> elementPool = new HashMap<>();


    // TOBE abstract
    public abstract MyElement create(MyElement parent, IModelElement vpElement);

    private boolean hasMyElementOf(String id) {
        return this.elementPool.containsKey(id);
    }

    private MyElement MyElementOf(String vpElement) {
        return this.elementPool.get(vpElement);
    }

    protected MyElement getOrCreateElement(
        IModelElement vpElement,
        MyElement parent,
        MyElementCreator createMyElement
    ) {
        if (hasMyElementOf(vpElement.getId())) {
            return MyElementOf(vpElement.getId());
        } else {
            return createMyElement.from(vpElement, parent);
        }
    }


    @FunctionalInterface
    protected static interface MyElementCreator {
        MyElement from(IModelElement vpElement, MyElement parent);
    }
}
