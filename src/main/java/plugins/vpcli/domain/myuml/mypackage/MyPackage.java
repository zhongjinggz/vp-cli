package plugins.vpcli.domain.myuml.mypackage;

import plugins.vpcli.domain.myuml.myclassifier.*;
import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;
import plugins.vpcli.domain.myuml.mycomponent.MyComponent;
import plugins.vpcli.domain.myuml.mynary.MyNary;
import plugins.vpcli.domain.myuml.myusecase.MyActor;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;

import java.util.ArrayList;
import java.util.List;

public class MyPackage extends MyElement {
    private boolean isSubpackage;
    private boolean isRectangle = false; // is System in reality, but systems are not a different type in puml , just a rectangle shape

    public MyPackage(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes
    ) {
        super(parent,
            id,
            name,
            MyElementType.PACKAGE,
            description,
            stereotypes
        );
    }

    // used by MyModel to indicate the element type
    public MyPackage(
        MyElement parent,
        String id,
        String name,
        MyElementType myElementType,
        String description,
        List<MyStereotype> stereotypes
    ) {
        super(parent,
            id,
            name,
            myElementType,
            description,
            stereotypes
        );
    }

    public MyPackage(String packageName, boolean isSubpackage) {
        super(packageName);
        this.isSubpackage = isSubpackage;
    }
    // old

    public MyPackage(String packageName
        , List<MyClass> classes
        , List<MyPackage> subPackages
        , List<MyNary> naries
        , boolean isSubpackage
        , boolean isRectangle) {

        super(packageName);

        super.addSubElements(classes);
        super.addSubElements(subPackages);
        super.addSubElements(naries);

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
        for (MyElement element : getSubElements()) {
            if (element instanceof MyClass) {
                result.add((MyClass) element);
            }
        }
        return result;
    }

    public List<MyPackage> getSubPackages() {
        List<MyPackage> result = new ArrayList<>();
        for (MyElement element : getSubElements()) {
            if (element instanceof MyPackage) {
                result.add((MyPackage) element);
            }
        }
        return result;
    }

    public void addSubPackage(MyPackage subPackage) {
        addSubElement(subPackage);
    }

    public List<MyNary> getNaries() {
        List<MyNary> result = new ArrayList<>();
        for (MyElement element : getSubElements()) {
            if (element instanceof MyNary) {
                result.add((MyNary) element);
            }
        }
        return result;
    }

    public List<MyActor> getActors() {
        List<MyActor> result = new ArrayList<>();
        for (MyElement element : getSubElements()) {
            if (element instanceof MyActor) {
                result.add((MyActor) element);
            }
        }
        return result;
    }

    public List<MyUseCase> getUseCases() {
        List<MyUseCase> result = new ArrayList<>();
        for (MyElement element : getSubElements()) {
            if (element instanceof MyUseCase) {
                result.add((MyUseCase) element);
            }
        }
        return result;
    }

    public List<MyComponent> getComponents() {
        List<MyComponent> result = new ArrayList<>();
        for (MyElement element : getSubElements()) {
            if (element instanceof MyComponent) {
                result.add((MyComponent) element);
            }
        }
        return result;
    }

}
