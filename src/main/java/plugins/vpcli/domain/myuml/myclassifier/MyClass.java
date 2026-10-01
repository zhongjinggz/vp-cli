package plugins.vpcli.domain.myuml.myclassifier;

import plugins.vpcli.domain.myuml.mycommon.MyElement;

import java.util.ArrayList;
import java.util.List;

public class MyClass extends MyElement {
    private boolean isAbstract;
    private List<AttributeData> attributes = new ArrayList<>();
    private List<MyOperation> operations = new ArrayList<>();
    private List<String> stereotypes;
    private boolean isInPackage;
    private String visibility;
    private String Uid;

    public MyClass(String name, boolean isAbstract, String visibility, boolean isInPackage) {
    	super(name);
        this.setAbstract(isAbstract);
        this.visibility = visibility;
        this.stereotypes = new ArrayList<>();
        this.setInPackage(isInPackage);
    }

    public MyClass(String name, boolean isInPackage) { // interfaces for component
    	super(name);
    	this.setInPackage(isInPackage);
    	this.stereotypes = new ArrayList<>();

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
	
	public List<String> getStereotypes() {
		return stereotypes;
	}

	public void setStereotypes(List<String> stereotypes) {
		this.stereotypes = stereotypes;
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

	public boolean isInPackage() {
		return isInPackage;
	}

	public void setInPackage(boolean isInPackage) {
		this.isInPackage = isInPackage;
	}

	public String getVisibility() {
		return visibility;
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String Uid) {
		this.Uid = Uid;
	}

}



