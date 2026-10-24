package plugins.vpcli.domain.myuml.myunknownelement;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

public class UnimplementedElement extends MyElement {

    public static final String MODEL_TYPE_TEXT = "UnimplementedElement";

    private String vpModelType;


    public UnimplementedElement(
        MyElement parent,
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
