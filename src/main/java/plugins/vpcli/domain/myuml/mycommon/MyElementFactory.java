package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class MyElementFactory {

    private final Logger logger;

    private final MyElementPool elementPool;
    private MyElementFactoryFactory factoryFactory;
    protected MyElementType myElementType;
    protected MyElementTypes myElementTypes;

    public MyElementFactory(MyElementPool myElementPool, MyElementType myElementType) {
        this(myElementPool, myElementType, getLogger(MyElementFactory.class));
    }

    // 仅用于单元测试，可注入 Logger
    MyElementFactory(MyElementPool myElementPool, MyElementType myElementType, Logger logger) {
        this.elementPool = myElementPool;
        this.myElementType = myElementType;
        this.logger = logger;
    }

    public void setFactoryFactory(MyElementFactoryFactory factoryFactory) {
        this.factoryFactory = factoryFactory;
    }

    public abstract MyElement reallyCreate(MyElement parent, IModelElement vpElement);

    public MyElement of(IModelElement vpElement) {
        MyElement myParentElement;
        var vpParentElement = vpElement.getParent();

        // 如果 parent 为空，意味着是顶层元素，其上级是项目本身
        if (vpParentElement == null) {
            myParentElement = elementPool.getMyProject();
        } else {
            var parentId = vpParentElement.getId();
            if (elementPool.contains(parentId)) {
                myParentElement = elementPool.get(parentId);
            } else {
                var factory = factoryFactory.of(vpParentElement.getModelType());

                // var factory = myElementType.getMyElementFactory();
                myParentElement = factory.of(vpParentElement);
            }
        }
        return createWithParent(myParentElement, vpElement);
    }

    private MyElement createWithParent(MyElement parent, IModelElement vpElement) {
        String id = vpElement.getId();

        if (elementPool.contains(id)) {
            return elementPool.get(id);
        } else {
            debug(vpElement); // to log, can be removed later
            var result = reallyCreate(parent, vpElement);
            elementPool.put(id, result);
            return result;
        }
    }

    private void debug(IModelElement vpElement) {
        var vpParent = vpElement.getParent();
        var vpId = vpElement.getId();
        String parentId;
        if (vpParent == null) {
            parentId = "Parent is null";
        } else {
            parentId = vpParent.getId();
        }
        logger.debug("Creating MyElement: "
            + " type="
            + vpElement.getModelType()
            + " name="
            + vpElement.getName()
            + " id=" + vpId
            + " parent="
            + parentId
        );
    }

    //call by MyElementTypes to create the cycle dependency:
    // MyElementTypes --> MyElementType --> MyElementFactory
    //     ^----------------------------------------|
    // Do not call it other than here
    void setMyElementTypes(MyElementTypes myElementTypes) {
       this.myElementTypes = myElementTypes;
    }
}
