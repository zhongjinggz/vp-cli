package plugins.vpcli.domain.myuml.mymodelrelationshipcontainer;

import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyNary;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import java.util.List;

public class MyModelRelationshipContainer extends MyElement {

    public MyModelRelationshipContainer(MyElement parent, String id, String name, MyElementType myElementtype, String description) {
        super(parent, id, name, myElementtype, description);

    }
}
