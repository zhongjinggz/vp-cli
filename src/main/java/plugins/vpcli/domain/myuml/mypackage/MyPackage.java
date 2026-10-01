package plugins.vpcli.domain.myuml.mypackage;

import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyComponent;
import plugins.vpcli.domain.myuml.myclassifier.MyNary;
import plugins.vpcli.domain.myuml.mydeployment.MyArtifact;
import plugins.vpcli.domain.myuml.myusecase.MyActor;

import java.util.ArrayList;
import java.util.List;

public class MyPackage extends MyElement {
    private boolean isSubpackage;
    private boolean isRectangle = false; // is System in reality, but systems are not a different type in puml , just a rectangle shape

    public MyPackage(String id, String name) {
        super(id, name);

    }
    public MyPackage(String packageName, boolean isSubpackage) {
        super(packageName);
        this.isSubpackage = isSubpackage;
    }

    public MyPackage(String packageName
        , List<MyClass> classes
        , List<MyPackage> subPackages
        , List<MyNary> naries
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
    public List<MyClass> getClasses() {
        List<MyClass> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyClass) {
                result.add((MyClass) element);
            }
        }
        return result;
    }

    public List<MyPackage> getSubPackages() {
        List<MyPackage> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyPackage) {
                result.add((MyPackage) element);
            }
        }
        return result;
    }

    public void addSubPackage(MyPackage subPackage) {
        addChild(subPackage);
    }

    public List<MyNary> getNaries() {
        List<MyNary> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyNary) {
                result.add((MyNary) element);
            }
        }
        return result;
    }

    public List<MyActor> getActors() {
        List<MyActor> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyActor) {
                result.add((MyActor) element);
            }
        }
        return result;
    }

    public List<MyUseCase> getUseCases() {
        List<MyUseCase> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyUseCase) {
                result.add((MyUseCase) element);
            }
        }
        return result;
    }

    public List<MyComponent> getComponents() {
        List<MyComponent> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyComponent) {
                result.add((MyComponent) element);
            }
        }
        return result;
    }

    public List<MyArtifact> getArtifacts() {
        List<MyArtifact> result = new ArrayList<>();
        for (MyElement element : getChildren()) {
            if (element instanceof MyArtifact) {
                result.add((MyArtifact) element);
            }
        }
        return result;
    }
}
