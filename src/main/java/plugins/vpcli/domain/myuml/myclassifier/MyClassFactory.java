package plugins.vpcli.domain.myuml.myclassifier;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MyClassFactory extends MyElementFactory {

    @Inject
    public MyClassFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyElement reallyBuild(MyElement parent, IModelElement vpElement, List<MyStereotype> stereotypes) {
        MyClass myClass = new MyClass(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes);

        // 仅当 vpElement 确为 IClass 时才遍历成员（避免对仅 stub 了 IModelElement 的既有测试强转失败）
        if (vpElement instanceof IClass) {
            IClass vpClass = (IClass) vpElement;
            populateAttributes(vpClass, myClass);
            populateOperations(vpClass, myClass);
        }

        return myClass;
    }

    private void populateAttributes(IClass vpClass, MyClass myClass) {
        var attributeIter = vpClass.attributeIterator();
        while (attributeIter.hasNext()) {
            IAttribute attribute = (IAttribute) attributeIter.next();
            MyAttribute attr = new MyAttribute(
                attribute.getVisibility(),
                attribute.getName(),
                attribute.getTypeAsString(),
                attribute.getInitialValueAsString(),
                attribute.getScope());
            myClass.addAttribute(attr);
        }
    }

    private void populateOperations(IClass vpClass, MyClass myClass) {
        var operationIter = vpClass.operationIterator();
        while (operationIter.hasNext()) {
            IOperation operation = (IOperation) operationIter.next();
            List<MyOperation.Parameter> parameters = new ArrayList<>();
            var paramIter = operation.parameterIterator();
            while (paramIter.hasNext()) {
                IParameter parameter = (IParameter) paramIter.next();
                parameters.add(new MyOperation.Parameter(
                    parameter.getName(),
                    parameter.getTypeAsString(),
                    parameter.getDefaultValueAsString()));
            }
            MyOperation op = new MyOperation(
                operation.getVisibility(),
                operation.getName(),
                operation.getReturnTypeAsString(),
                operation.isAbstract(),
                parameters,
                operation.getScope());
            myClass.addOperation(op);
        }
    }
}
