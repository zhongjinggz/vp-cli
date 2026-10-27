package plugins.vpcli.domain.myuml_old.mypackage_old;

import plugins.vpcli.domain.myuml.myclassifier.*;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mycomponent.MyComponent;
import plugins.vpcli.domain.myuml.mynary.MyNary;
import plugins.vpcli.domain.myuml_old.myusecase_old.MyActor;
import plugins.vpcli.domain.myuml_old.myusecase_old.MyUseCase_old;

import java.util.ArrayList;
import java.util.List;

public class MyPackage_old extends MyElement_old2 {
    private boolean isSubpackage;
    private boolean isRectangle = false; // is System in reality, but systems are not a different type in puml , just a rectangle shape

    public MyPackage_old(
        MyElement_old2 parent,
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
    public MyPackage_old(
        MyElement_old2 parent,
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

    public MyPackage_old(String packageName, boolean isSubpackage) {
        super(packageName);
        this.isSubpackage = isSubpackage;
    }
    // old

    public MyPackage_old(String packageName
        , List<MyClass> classes
        , List<MyPackage_old> subPackages
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
        for (MyElement_old2 element : getSubElements()) {
            if (element instanceof MyClass) {
                result.add((MyClass) element);
            }
        }
        return result;
    }

    public List<MyPackage_old> getSubPackages() {
        List<MyPackage_old> result = new ArrayList<>();
        for (MyElement_old2 element : getSubElements()) {
            if (element instanceof MyPackage_old) {
                result.add((MyPackage_old) element);
            }
        }
        return result;
    }

    public void addSubPackage(MyPackage_old subPackage) {
        addSubElement(subPackage);
    }

    public List<MyNary> getNaries() {
        List<MyNary> result = new ArrayList<>();
        for (MyElement_old2 element : getSubElements()) {
            if (element instanceof MyNary) {
                result.add((MyNary) element);
            }
        }
        return result;
    }

    public List<MyActor> getActors() {
        List<MyActor> result = new ArrayList<>();
        for (MyElement_old2 element : getSubElements()) {
            if (element instanceof MyActor) {
                result.add((MyActor) element);
            }
        }
        return result;
    }

    public List<MyUseCase_old> getUseCases() {
        List<MyUseCase_old> result = new ArrayList<>();
        for (MyElement_old2 element : getSubElements()) {
            if (element instanceof MyUseCase_old) {
                result.add((MyUseCase_old) element);
            }
        }
        return result;
    }

    public List<MyComponent> getComponents() {
        List<MyComponent> result = new ArrayList<>();
        for (MyElement_old2 element : getSubElements()) {
            if (element instanceof MyComponent) {
                result.add((MyComponent) element);
            }
        }
        return result;
    }

}
