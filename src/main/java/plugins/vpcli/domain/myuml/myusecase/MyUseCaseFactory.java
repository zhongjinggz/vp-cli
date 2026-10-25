package plugins.vpcli.domain.myuml.myusecase;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.myelement.MyElementFactory;
import plugins.vpcli.domain.myuml.myelement.MyElementPool;

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
    public MyUseCase reallyBuild(
        MyElement parent,
        IModelElement vpElement,
        List<MyStereotype> stereotypes
    ) {
        return new MyUseCase(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription(),
            stereotypes);
    }
}
