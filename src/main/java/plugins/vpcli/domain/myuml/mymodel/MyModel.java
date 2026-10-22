package plugins.vpcli.domain.myuml.mymodel;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;
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