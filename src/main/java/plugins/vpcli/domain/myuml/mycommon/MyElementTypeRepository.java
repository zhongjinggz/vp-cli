package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.mycommon.myelementtype.*;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;

import java.util.HashMap;
import java.util.Map;

public class MyElementTypeRepository {
    private final Map<String, MyElementType> bufferByString = new HashMap<>();
    private final Map<Class<? extends MyElementType>, MyElementType> bufferByClass = new HashMap<>();

    public MyElementTypeRepository() {
        register(new MyElementTypeProject());
        register(new MyElementTypeClass());
        register(new MyElementTypePackage());
        register(new MyElementTypeModel());
        register(new MyElementTypeUseCase());
        register(new MyElementTypeDiagram());
        register(new MyElementTypeAssociation());
        register(new MyElementTypeModelRelationshipContainer());
    }

    private void register(MyElementType myElementType) {
        bufferByString.put(myElementType.getVPModelType(), myElementType);
        bufferByClass.put(myElementType.getClass(), myElementType);
    }

    public void register(MyElementFactory myElementFactory) {
        var myElementType = findByClass(myElementFactory.getMyElementType().getClass());
        myElementType.setMyElementFactory(myElementFactory);
    }

    public void register(PumlElementExporter pumlElementExporter, Class<? extends MyElementType> classOfMyElementType) {
        var myElementType = findByClass(classOfMyElementType);
        myElementType.setPumlElementExporter(pumlElementExporter);
    }

    public MyElementType findByVPModelType(String vpModelType) {
        MyElementType myElementType = bufferByString.get(vpModelType);
        if (myElementType == null) {
            throw new IllegalArgumentException("Unknown visual paradigm model type: " + vpModelType);
        }
        return myElementType;
    }

    public MyElementType findByClass(Class<? extends MyElementType> myElementTypeClass) {
        MyElementType myElementType = bufferByClass.get(myElementTypeClass);
        if (myElementType == null) {
            throw new IllegalArgumentException("Unknown MyElementType class: " + myElementTypeClass);
        }
        return myElementType;
    }
}
