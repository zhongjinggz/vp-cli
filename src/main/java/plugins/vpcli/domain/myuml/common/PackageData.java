package plugins.vpcli.domain.myuml.common;

import plugins.vpcli.domain.myuml.usecase.UseCaseData;
import plugins.vpcli.domain.myuml.mystatemachine.BaseWithSemanticsData;
import plugins.vpcli.domain.myuml.classifier.ClassData;
import plugins.vpcli.domain.myuml.classifier.ComponentData;
import plugins.vpcli.domain.myuml.classifier.NaryData;
import plugins.vpcli.domain.myuml.deployment.ArtifactData;
import plugins.vpcli.domain.myuml.usecase.ActorData;

import java.util.ArrayList;
import java.util.List;

public class PackageData extends BaseWithSemanticsData {
    private List<ClassData> classes = new ArrayList<>();
    private List<PackageData> subPackages = new ArrayList<>(); // Nested packages if any
    private List<NaryData> naries = new ArrayList<>();
    private List<ActorData> actors = new ArrayList<>();
    private List<UseCaseData> useCases = new ArrayList<>();
    private final List<ComponentData> components = new ArrayList<>();
	private final List<ArtifactData> artifacts = new ArrayList<>();
    private boolean isSubpackage;
    private boolean isRectangle = false; // is System in reality, but systems are not a different type in puml , just a rectangle shape
    private String Uid;

    public PackageData(String packageName, boolean isSubpackage) {
    	super(packageName);
    	this.isSubpackage = isSubpackage;
    	
    }
    public PackageData(String packageName, List<ClassData> classes, List<PackageData> subPackages, List<NaryData> naries, boolean isSubpackage, boolean isRectangle) {
        super(packageName);
        this.classes = classes != null ? classes : new ArrayList<>();
        this.subPackages = subPackages != null ? subPackages : new ArrayList<>();
        this.setNaries(naries != null ? naries : new ArrayList<>());
        this.setSubpackage(isSubpackage);
        this.useCases = useCases != null ? useCases : new ArrayList<>();
        this.actors = actors != null ? actors : new ArrayList<>();
        this.classes = classes != null ? classes : new ArrayList<>();
        this.isRectangle = isRectangle;
    }

    
    public List<ClassData> getClasses() {
        return classes;
    }

    public List<PackageData> getSubPackages() {
        return subPackages;
    }

	public boolean isSubpackage() {
		return isSubpackage;
	}

	public void setSubpackage(boolean isSubpackage) {
		this.isSubpackage = isSubpackage;
	}

	public List<NaryData> getNaries() {
		return naries;
	}

	public void setNaries(List<NaryData> naries) {
		this.naries = naries;
	}

	public List<ActorData> getActors() {
		return actors;
	}

	public List<UseCaseData> getUseCases() {
		return useCases;
	}

	public boolean isRectangle() {
		return this.isRectangle;
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}
	
	public List<ComponentData> getComponents() {
		return components;
	}

	public List<ArtifactData> getArtifacts() {
		return artifacts;
	}
}
