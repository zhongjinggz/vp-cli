package plugins.vpcli.domain.myuml.mymodel;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackage_old;

import java.util.List;

public class MyModel extends MyPackage_old {
    public MyModel(
        MyElement_old2 parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes
    ) {
        super(parent, id, name, MyElementType.MODEL, description, stereotypes);
    }
}