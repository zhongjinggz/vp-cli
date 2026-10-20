package plugins.vpcli.domain.myuml.mycommon;

import plugins.vpcli.domain.myuml.mycommon.myelementtype.*;

import java.util.HashMap;
import java.util.Map;

public class MyElementTypeRepository {
    private final Map<String, MyElementType> bufferByString = new HashMap<>();
    private final Map<Class<? extends MyElementType>, MyElementType> bufferByClass = new HashMap<>();

//    public final MyElementTypeProject PROJECt;
//    public final MyElementTypeClass CLASS;
//    public final MyElementTypePackage PACKAGE;
//    public final MyElementTypeModel MODEL;
//    public final MyElementTypeUseCase USE_CASE;
//    public final MyElementTypeDiagram DIAGRAM;
//    public final MyElementTypeAssociation ASSOCIATION;
//    public final MyElementTypeModelRelationshipContainer MODEL_RELATIONSHIP_CONTAINER;


    public MyElementTypeRepository() {
//        this.PROJECt = new MyElementTypeProject();
//        register(this.PROJECt);
//
//        this.CLASS = new MyElementTypeClass();
//        register(this.CLASS);
//
//        this.PACKAGE = new MyElementTypePackage();
//        register(this.PACKAGE);
//
//        this.MODEL = new MyElementTypeModel();
//        register(this.MODEL);
//
//        this.USE_CASE = new MyElementTypeUseCase();
//        register(this.USE_CASE);
//
//        this.DIAGRAM = new MyElementTypeDiagram();
//        register(this.DIAGRAM);
//
//        this.ASSOCIATION = new MyElementTypeAssociation();
//        register(this.ASSOCIATION);
//
//        this.MODEL_RELATIONSHIP_CONTAINER = new MyElementTypeModelRelationshipContainer();
//        register(this.MODEL_RELATIONSHIP_CONTAINER);

    }

    private void register(MyElementType myElementType) {
        bufferByString.put(myElementType.getVPModelType(), myElementType);
        bufferByClass.put(myElementType.getClass(), myElementType);
    }

}
