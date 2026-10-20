package plugins.vpcli.domain.myuml.myusecase;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;

import jakarta.inject.Singleton;

@Singleton
public class MyUseCaseFactory extends MyElementFactory {

    public MyUseCaseFactory(
        MyElementPool myElementPool
//        , MyElementTypeUseCase myElementType
//        , MyElementTypeRepository myElementTypeRepository
    ) {
        super(myElementPool
        );
    }

    @Override
    public MyUseCase reallyCreate(MyElement parent, IModelElement vpElement) {
        return new MyUseCase(
            parent,
            vpElement.getId(),
            vpElement.getName(),
            vpElement.getDescription()
        );
    }
}
