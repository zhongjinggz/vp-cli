package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.util.ArrayList;
import java.util.List;

public class MyConverter {
    public List<MyPackage> fromVPElementsToPackages(IModelElement[] vpElements) {
        List<MyPackage> result = new ArrayList<>();
        for (var aVPElement : vpElements) {
            if (isPackage(aVPElement)) {
                var aPackage = fromVPElement(aVPElement);
                var subPackages = fromVPElementsToPackages(aVPElement.toChildArray());
                aPackage.addChildren(subPackages);
                result.add(aPackage);
            }
        }
        return result;
    }

    boolean isPackage(IModelElement element) {
        String type = element.getModelType();
        return IModelElementFactory.MODEL_TYPE_PACKAGE.equals(type)
            || IModelElementFactory.MODEL_TYPE_MODEL.equals(type);
    }

    MyPackage fromVPElement(IModelElement vpElement) {
        return new MyPackage(vpElement.getId(), vpElement.getName());
    }
}
