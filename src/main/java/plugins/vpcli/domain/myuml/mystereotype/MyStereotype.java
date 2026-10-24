package plugins.vpcli.domain.myuml.mystereotype;

import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementType;

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
