package plugins.vpcli.domain.myuml.myusecase;

import com.vp.plugin.model.IModelElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;

public class MyUseCaseFactory extends MyElementFactory {

    @Override
    public MyUseCase create(MyElement parent, IModelElement vpElement) {
        return (MyUseCase) getOrCreateElement(
            vpElement, parent,
            (ele, par) ->
                new MyUseCase(
                    par,
                    ele.getId(),
                    ele.getName(),
                    ele.getDescription()
                )
        );
    }
}
