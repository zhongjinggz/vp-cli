package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

public class MyAssociation extends MyElement {

    public MyAssociation(
        MyElement parent,
        String id,
        String name,
        String Description,
        List<MyStereotype> stereotypes) {
        super(parent,
            id,
            name,
            MyElementType.ASSOCIATION,
            Description,
            stereotypes
        );
    }


}
