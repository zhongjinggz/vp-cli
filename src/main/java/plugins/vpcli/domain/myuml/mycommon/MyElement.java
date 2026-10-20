package plugins.vpcli.domain.myuml.mycommon;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.MyElementType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MyElement {
    private String id = "";
    private String name = "";
    private String description = "";
    private MyElementType myElementType;
    private List<String> stereotypes = new ArrayList<>();
    private ElementType elementType;

    protected MyElement parentElement;
    protected final List<MyElement> subElements = new ArrayList<>();

    // for new elements
    public MyElement(
        MyElement parentElement,
        String id,
        String name,
        ElementType elementType,
//        MyElementType myElementType,
        String description
    ) {
        this.parentElement = parentElement;
        this.id = id;
        this.name = name;
        this.description = description;
        this.elementType = elementType;
//        this.myElementType = myElementType;
        this.parentElement.addSubElement(this);
    }

    // old
    public MyElement(String name) {
        this.name = name;
    }

    //for MyProject
    public MyElement(String id, String name, MyElementType myElementType, ElementType elementType) {
        this.id = id;
        this.name = name;
        this.myElementType = myElementType;
        this.elementType = elementType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyElementType getMyElementType() {
        return myElementType;
    }

    public String getDescription() {
        return description;
    }

    public List<MyElement> getSubElements() {
        return Collections.unmodifiableList(subElements);
    }

    public void addSubElement(MyElement subElement) {
        this.subElements.add(subElement);
    }

    public void addSubElements(@NotNull List<? extends MyElement> elements) {
        Objects.requireNonNull(elements, "Bug: elements can't be null");
        for (var element : elements) {
            addSubElement(element);
        }
    }

    public MyElement getParentElement() {
        return parentElement;
    }

    public ElementType getElementType() {
        return elementType;
    }
}
