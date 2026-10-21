package plugins.vpcli.domain.myuml.mycommon;

import java.util.ArrayList;

public class MyStereotype extends MyElement {

    public MyStereotype(
        MyElement parentElement,
        String id,
        String name,
        String description
    ) {
        super(
            parentElement,
            id,
            name,
            MyElementType.STEREOTYPE,
            description,
            new ArrayList<MyStereotype>()
        );
    }
}
