package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class MyElementFactory {

    private final Logger logger;

    private MyElementPool elementPool;
    protected MyElementType myElementType;
    protected MyElementTypeRepository myElementTypeRepository;

    public MyElementFactory(MyElementPool myElementPool
        , MyElementType myElementType
        , MyElementTypeRepository myElementTypeRepository) {
        this(myElementPool
            , myElementType
            , myElementTypeRepository
            , getLogger(MyElementFactory.class));
    }

    // 仅用于单元测试，可注入 Logger
    MyElementFactory(MyElementPool myElementPool
        , MyElementType myElementType
        , MyElementTypeRepository myElementTypeRepository
        , Logger logger
    ) {
        this.elementPool = myElementPool;
        this.myElementType = myElementType;
        this.myElementTypeRepository = myElementTypeRepository;
        this.logger = logger;


        // below statement will build the circle dependency:
        // MyElementTypeRepository --> MyElementType --> MyElementFactory
        //     ^----------------------------------------|
        // Even though circle circle dependency is not a best practices in normal case,
        // but it is necessary here
        //this.myElementTypeRepository.register(this);
        if (myElementType != null) {
            myElementType.setMyElementFactory(this);
        }


    }

    public MyElementFactory() {
        this.logger = getLogger(MyElementFactory.class);
    }

    public void setFactoryFactory(MyElementFactoryFactory factoryFactory) {
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
                //var factory = factoryFactory.of(vpParentElement.getModelType());

                // var factory = myElementType.getMyElementFactory();
                var factory = myElementTypeRepository
                    .findByVPModelType(vpParentElement.getModelType())
                    .getMyElementFactory();

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
        logger.debug("Creating MyElement:  type={} name={} id={} parent={}"
            , vpElement.getModelType()
            , vpElement.getName()
            , vpId
            , parentId
        );
    }

    public MyElementType getMyElementType() {
        return myElementType;
    }
}
