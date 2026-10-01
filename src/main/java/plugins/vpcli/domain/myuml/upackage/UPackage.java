package plugins.vpcli.domain.myuml.upackage;

import plugins.vpcli.domain.myuml.ucommon.UElement;
import plugins.vpcli.domain.myuml.uusecase.UUseCase;
import plugins.vpcli.domain.myuml.uclassifier.UClass;
import plugins.vpcli.domain.myuml.uclassifier.UComponent;
import plugins.vpcli.domain.myuml.uclassifier.NaryData;
import plugins.vpcli.domain.myuml.udeployment.UArtifact;
import plugins.vpcli.domain.myuml.uusecase.ActorData;

import java.util.ArrayList;
import java.util.List;

public class UPackage extends UElement {
    private boolean isSubpackage;
    private boolean isRectangle = false; // is System in reality, but systems are not a different type in puml , just a rectangle shape

    public UPackage(String id, String name) {
        super(id, name);

    }
    public UPackage(String packageName, boolean isSubpackage) {
        super(packageName);
        this.isSubpackage = isSubpackage;
    }

    public UPackage(String packageName
        , List<UClass> classes
        , List<UPackage> subPackages
        , List<NaryData> naries
        , boolean isSubpackage
        , boolean isRectangle) {

        super(packageName);

        super.addChildren(classes);
        super.addChildren(subPackages);
        super.addChildren(naries);

        this.setSubpackage(isSubpackage);
        this.isRectangle = isRectangle;
    }


    public boolean isSubpackage() {
        return isSubpackage;
    }

    public void setSubpackage(boolean isSubpackage) {
        this.isSubpackage = isSubpackage;
    }

    public boolean isRectangle() {
        return this.isRectangle;
    }

    // TODO 抽取下述个方法的重复代码
    public List<UClass> getClasses() {
        List<UClass> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof UClass) {
                result.add((UClass) element);
            }
        }
        return result;
    }

    public List<UPackage> getSubPackages() {
        List<UPackage> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof UPackage) {
                result.add((UPackage) element);
            }
        }
        return result;
    }

    public void addSubPackage(UPackage subPackage) {
        addChild(subPackage);
    }

    public List<NaryData> getNaries() {
        List<NaryData> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof NaryData) {
                result.add((NaryData) element);
            }
        }
        return result;
    }

    public List<ActorData> getActors() {
        List<ActorData> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof ActorData) {
                result.add((ActorData) element);
            }
        }
        return result;
    }

    public List<UUseCase> getUseCases() {
        List<UUseCase> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof UUseCase) {
                result.add((UUseCase) element);
            }
        }
        return result;
    }

    public List<UComponent> getComponents() {
        List<UComponent> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof UComponent) {
                result.add((UComponent) element);
            }
        }
        return result;
    }

    public List<UArtifact> getArtifacts() {
        List<UArtifact> result = new ArrayList<>();
        for (UElement element : getChildren()) {
            if (element instanceof UArtifact) {
                result.add((UArtifact) element);
            }
        }
        return result;
    }
}
