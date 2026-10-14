package plugins.vpcli.domain.myuml.mypackage;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

public class MyModel extends MyPackage {
    public MyModel(
        MyElement parent,
        String id,
        String name,
        MyElementType myElementtype,
        String description
    ) {
        super(parent, id, name, myElementtype, description);
    }
}