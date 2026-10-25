package plugins.vpcli.domain.myuml.myelement;

import com.vp.plugin.model.IModelElement;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class MyElementFactory {

    private static final Logger logger = LoggerFactory.getLogger(MyElementFactory.class);

    private final MyElementPool elementPool;
    private MyElementFactoryFactory factoryFactory;

    protected MyElementFactory(MyElementPool myElementPool) {
        this.elementPool = myElementPool;
    }

    // set by MyElementFactoryFactory, by-direction navigation
    public void setFactoryFactory(MyElementFactoryFactory factoryFactory) {
        this.factoryFactory = factoryFactory;
    }

    public abstract MyElement reallyBuild(MyElement parent, IModelElement vpElement, List<MyStereotype> myStereotypes);

    public MyElement build(IModelElement vpElement) {
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
        return buildMySelfWithParent(myParentElement, vpElement);
    }

    // use proper factory to build
    protected MyElement buildRelatedElement(IModelElement vpElement) {
        var factory = factoryFactory.of(vpElement.getModelType());
        return factory.build(vpElement);
    }

    protected MyElement buildMySelfWithParent(MyElement parent, IModelElement vpElement) {
        String id = vpElement.getId();

        if (elementPool.contains(id)) {
            return elementPool.get(id);
        } else {
            debug(vpElement); // to log, can be removed later

            List<MyStereotype> myStereotypes = buildMyStereotypes(vpElement);

            var result = reallyBuild(parent, vpElement, myStereotypes);
            elementPool.put(id, result);
            return result;
        }
    }

    // May simplify Stereotype, not to think it an element?
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
