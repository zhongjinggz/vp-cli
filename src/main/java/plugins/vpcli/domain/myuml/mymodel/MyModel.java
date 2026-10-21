package plugins.vpcli.domain.myuml.mymodel;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;

public class MyModel extends MyPackage {
    public MyModel(
        MyElement parent,
        String id,
        String name,
        String description
    ) {
        super(parent, id, name
            ,
            description);
    }
}