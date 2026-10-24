package plugins.vpcli.domain.myuml.myusecase;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

public class MyUseCase extends MyElement {
    private String Uid;


    public MyUseCase(
        MyElement parent,
        String id,
        String name,
        String Description,
        List<MyStereotype> stereotypes) {
        super(parent, id, name, MyElementType.USE_CASE
            , Description, stereotypes);
    }

}
