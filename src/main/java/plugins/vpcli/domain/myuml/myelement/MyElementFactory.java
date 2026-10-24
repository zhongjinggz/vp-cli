package plugins.vpcli.domain.myuml.myelement;

import com.vp.plugin.model.IModelElement;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

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
                myParentElement = buildRelatedElement(vpParentElement);
            }
        }
        return createWithParent(myParentElement, vpElement);
    }

    // use proper factory to build
    protected MyElement buildRelatedElement(IModelElement vpElement) {
        var factory = factoryFactory.of(vpElement.getModelType());
        return factory.of(vpElement);
    }

    private MyElement createWithParent(MyElement parent, IModelElement vpElement) {
        String id = vpElement.getId();

        if (elementPool.contains(id)) {
            return elementPool.get(id);
        } else {
            debug(vpElement); // to log, can be removed later

            List<MyStereotype> myStereotypes = buildMyStereotypes(vpElement);

            var result = reallyCreate(parent, vpElement, myStereotypes);
            elementPool.put(id, result);
            return result;
        }
    }

    private @NonNull List<MyStereotype> buildMyStereotypes(IModelElement vpElement) {
        List<MyStereotype> result = new ArrayList<>();

        var vpStereotypes = vpElement.toStereotypeModelArray();
        if (vpStereotypes != null) {
            for (var aStereotype : vpStereotypes) {
                MyElement myElement = buildRelatedElement(aStereotype);
                result.add((MyStereotype) myElement);
            }
        }

        return result;
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
