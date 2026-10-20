package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import plugins.vpcli.domain.myuml.mycommon.ElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;

public class MyModelRelationshipContainer extends MyElement {

    public MyModelRelationshipContainer(MyElement parent, String id, String name, String description) {
        super(parent, id, name, ElementType.RELATIONSHIPS
            , description);

    }
}
