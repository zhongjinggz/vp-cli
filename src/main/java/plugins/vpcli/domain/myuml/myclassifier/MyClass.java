package plugins.vpcli.domain.myuml.myclassifier;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.ArrayList;
import java.util.List;

public class MyClass extends MyElement_old2 {
    private boolean isAbstract;
    private List<MyAttribute> attributes = new ArrayList<>();
    private List<MyOperation> operations = new ArrayList<>();

    public MyClass(
        MyElement_old2 parent, String id,
        String name,
        String description,
        List<MyStereotype> stereotypes) {
        super(parent,
            id,
            name,
            MyElementType.CLASS,
            description,
            stereotypes
        );
        isAbstract = false;

    }

    public void addAttribute(MyAttribute attribute) {
        getAttributes().add(attribute);
    }

    public void addOperation(MyOperation operation) {
        getOperations().add(operation);
    }

    public List<MyAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<MyAttribute> attributes) {
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



