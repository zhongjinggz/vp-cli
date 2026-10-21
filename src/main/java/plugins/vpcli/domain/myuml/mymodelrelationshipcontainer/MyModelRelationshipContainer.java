package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.ArrayList;

public class MyModelRelationshipContainer extends MyElement {

    public MyModelRelationshipContainer(MyElement parent, String id, String name, String description) {
        super(parent,
            id,
            name,
            MyElementType.RELATIONSHIPS,
            description,
            new ArrayList<MyStereotype>()
        );

    }
}
