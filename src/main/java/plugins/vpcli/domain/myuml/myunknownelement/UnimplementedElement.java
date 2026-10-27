package plugins.vpcli.domain.myuml.myunknownelement;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

public class UnimplementedElement extends MyElement_old2 {

    public static final String MODEL_TYPE_TEXT = "UnimplementedElement";

    private String vpModelType;


    public UnimplementedElement(
        MyElement_old2 parent,
        String id,
        String name,
        String Description,
        List<MyStereotype> stereotypes,
        @NotNull String vpModelType
    ) {
        super(
            parent,
            id,
            name,
            MyElementType.UNIMPLEMENTED_ELEMENT,
            Description, stereotypes
        );
        this.vpModelType = vpModelType;
    }

    public String getVpModelType() {
        return vpModelType;
    }


}
