package plugins.vpcli.domain.myuml.myusecase;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class MyUseCaseFactory extends MyElementFactory {

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
