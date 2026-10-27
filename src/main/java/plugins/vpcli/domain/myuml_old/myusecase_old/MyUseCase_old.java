package plugins.vpcli.domain.myuml_old.myusecase_old;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

public class MyUseCase_old extends MyElement_old2 {
    private String Uid;


    public MyUseCase_old(
        MyElement_old2 parent,
        String id,
        String name,
        String Description,
        List<MyStereotype> stereotypes) {
        super(parent, id, name, MyElementType.USE_CASE
            , Description, stereotypes);
    }

}
