package plugins.vpcli.domain.myuml.myclassifier;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import java.util.ArrayList;
import java.util.List;

public class MyClass extends MyElement {
    private boolean isAbstract;
    private List<AttributeData> attributes = new ArrayList<>();
    private List<MyOperation> operations = new ArrayList<>();
    // private List<String> stereotypes;
    // private boolean isInPackage;
    // private String visibility;
    //private String Uid;

    public MyClass(
        MyElement parent, String id,
        String name,
        MyElementType myElementType,
        String description
    ) {
        super(parent, id, name, myElementType, description);
        isAbstract = false;

    }

    public void addAttribute(AttributeData attribute) {
        getAttributes().add(attribute);
    }

    public void addOperation(MyOperation operation) {
        getOperations().add(operation);
    }

    public List<AttributeData> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeData> attributes) {
        this.attributes = attributes;
    }

    public List<MyOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<MyOperation> operations) {
        this.operations = operations;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }


}



