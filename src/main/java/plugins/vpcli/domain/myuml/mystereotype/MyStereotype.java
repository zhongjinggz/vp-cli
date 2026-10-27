package plugins.vpcli.domain.myuml.mystereotype;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;

import java.util.ArrayList;

public class MyStereotype extends MyElement_old2 {

    public MyStereotype(
        MyElement_old2 parentElement,
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
