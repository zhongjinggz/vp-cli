package plugins.vpcli.domain.myuml_old.myusecase_old;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementFactory;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementPool;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

@Singleton
public class MyUseCaseFactory extends MyElementFactory {

    @Inject
    public MyUseCaseFactory(MyElementPool myElementPool) {
        super(myElementPool);
    }

    @Override
    public MyUseCase_old reallyCreate(
        MyElement_old2 parent,
        IModelElement vpElement,
        List<MyStereotype> stereotypes
    ) {
        return new MyUseCase_old(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes);
    }
}
