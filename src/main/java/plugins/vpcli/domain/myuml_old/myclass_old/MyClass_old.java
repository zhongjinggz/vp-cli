package plugins.vpcli.domain.myuml_old.myclass_old;

import plugins.vpcli.domain.myuml.mycommon.MyElement_old;

import java.util.ArrayList;
import java.util.List;

public class MyClass_old extends MyElement_old {
    private boolean isAbstract;
    private List<MyAttribute_old> attributes = new ArrayList<>();
    private List<MyOperation_old> operations = new ArrayList<>();
    private List<String> stereotypes;
    private boolean isInPackage;
    private String visibility;
    private String Uid;

    public MyClass_old(String name, boolean isAbstract, String visibility, boolean isInPackage) {
    	super(name);
        this.setAbstract(isAbstract);
        this.visibility = visibility;
        this.stereotypes = new ArrayList<>();
        this.setInPackage(isInPackage);
    }

    public MyClass_old(String name, boolean isInPackage) { // interfaces for component
    	super(name);
    	this.setInPackage(isInPackage);
    	this.stereotypes = new ArrayList<>();

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
	
	public List<String> getStereotypes() {
		return stereotypes;
	}

	public void setStereotypes(List<String> stereotypes) {
		this.stereotypes = stereotypes;
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



