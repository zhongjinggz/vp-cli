package plugins.vpcli.domain.myuml.mymodel;

import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.util.List;

public class MyModel extends MyPackage {
    public MyModel(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes
    ) {
        super(parent, id, name, MyElementType.MODEL, description, stereotypes);
    }
}