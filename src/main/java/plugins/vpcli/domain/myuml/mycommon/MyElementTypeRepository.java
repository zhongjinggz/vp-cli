package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.mycommon.myelementtype.*;
import plugins.vpcli.domain.outputmodel.PumlElementExporter;

import java.util.HashMap;
import java.util.Map;

public class MyElementTypeRepository {
    private final Map<String, MyElementType> bufferByString = new HashMap<>();
    private final Map<Class<? extends MyElementType>, MyElementType> bufferByClass = new HashMap<>();

    public final MyElementTypeProject PROJECt;
    public final MyElementTypeClass CLASS;
    public final MyElementTypePackage PACKAGE;
    public final MyElementTypeModel MODEL;
    public final MyElementTypeUseCase USE_CASE;
    public final MyElementTypeDiagram DIAGRAM;
    public final MyElementTypeAssociation ASSOCIATION;
    public final MyElementTypeModelRelationshipContainer MODEL_RELATIONSHIP_CONTAINER;


    public MyElementTypeRepository() {
        this.PROJECt = new MyElementTypeProject();
        register(this.PROJECt);

        this.CLASS = new MyElementTypeClass();
        register(this.CLASS);

        this.PACKAGE = new MyElementTypePackage();
        register(this.PACKAGE);

        this.MODEL = new MyElementTypeModel();
        register(this.MODEL);

        this.USE_CASE = new MyElementTypeUseCase();
        register(this.USE_CASE);

        this.DIAGRAM = new MyElementTypeDiagram();
        register(this.DIAGRAM);

        this.ASSOCIATION = new MyElementTypeAssociation();
        register(this.ASSOCIATION);

        this.MODEL_RELATIONSHIP_CONTAINER = new MyElementTypeModelRelationshipContainer();
        register(this.MODEL_RELATIONSHIP_CONTAINER);

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

    public MyElementType findByClass(Class<? extends MyElementType> classOfMyElementType) {
        MyElementType myElementType = bufferByClass.get(classOfMyElementType);
        if (myElementType == null) {
            throw new IllegalArgumentException("Unknown MyElementType class: " + classOfMyElementType);
        }
        return myElementType;
    }
}
