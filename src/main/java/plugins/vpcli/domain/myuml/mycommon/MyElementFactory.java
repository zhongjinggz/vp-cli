package plugins.vpcli.domain.myuml.mycommon;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class MyElementFactory {

    private final Logger logger;

    private final MyElementPool elementPool;
    private MyElementFactoryFactory factoryFactory;

    public MyElementFactory(
        MyElementPool myElementPool
    ) {
        this(myElementPool,
            getLogger(MyElementFactory.class));
    }

    // 仅用于单元测试，可注入 Logger
    MyElementFactory(MyElementPool myElementPool,
                     Logger logger
    ) {
        this.elementPool = myElementPool;
        this.logger = logger;

    }

    // set by MyElementFactoryFactory, by-direction navigation
    public void setFactoryFactory(MyElementFactoryFactory factoryFactory) {
        this.factoryFactory = factoryFactory;
    }

    public abstract MyElement reallyCreate(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes);

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

            List<MyStereotype> myStereotypes = createMyStereotypes(vpElement);

            var result = reallyCreate(parent, vpElement, myStereotypes);
            elementPool.put(id, result);
            return result;
        }
    }

    private @NonNull List<MyStereotype> createMyStereotypes(IModelElement vpElement) {
        List<MyStereotype> myStereotypes = new ArrayList<>();
        var stereotypeFactory = factoryFactory.of(IModelElementFactory.MODEL_TYPE_STEREOTYPE);

        var vpStereotypes = vpElement.toStereotypeModelArray();
        if (stereotypeFactory != null && vpStereotypes != null) {
            for (var aStereotype : vpStereotypes) {
                myStereotypes.add((MyStereotype) stereotypeFactory.of(aStereotype));
            }
        }

        return myStereotypes;
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
}
