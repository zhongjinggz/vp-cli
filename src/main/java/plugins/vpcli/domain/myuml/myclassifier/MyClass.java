package plugins.vpcli.domain.myuml.myclassifier;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml_old.myclass_old.MyAttribute_old;
import plugins.vpcli.domain.myuml_old.myclass_old.MyOperation_old;

import java.util.ArrayList;
import java.util.List;

public class MyClass extends MyElement {
    private boolean isAbstract;
    private List<MyAttribute_old> attributes = new ArrayList<>();
    private List<MyOperation_old> operations = new ArrayList<>();

    public MyClass(
        MyElement parent, String id,
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

    public void addAttribute(MyAttribute_old attribute) {
        getAttributes().add(attribute);
    }

    public void addOperation(MyOperation_old operation) {
        getOperations().add(operation);
    }

    public List<MyAttribute_old> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<MyAttribute_old> attributes) {
        this.attributes = attributes;
    }

    public List<MyOperation_old> getOperations() {
        return operations;
    }

    public void setOperations(List<MyOperation_old> operations) {
        this.operations = operations;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }


}



