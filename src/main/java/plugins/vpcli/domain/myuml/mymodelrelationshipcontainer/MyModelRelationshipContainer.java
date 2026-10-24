package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

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
