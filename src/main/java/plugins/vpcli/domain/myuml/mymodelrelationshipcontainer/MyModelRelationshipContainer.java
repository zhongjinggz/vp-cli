package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.ArrayList;

public class MyModelRelationshipContainer extends MyElement_old2 {

    public MyModelRelationshipContainer(MyElement_old2 parent, String id, String name, String description) {
        super(parent,
            id,
            name,
            MyElementType.RELATIONSHIPS,
            description,
            new ArrayList<MyStereotype>()
        );

    }
}
