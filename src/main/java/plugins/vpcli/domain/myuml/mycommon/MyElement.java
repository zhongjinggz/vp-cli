package plugins.vpcli.domain.myuml.mycommon;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MyElement {
    private String id = "";
    private String name = "";
    private String description = "";
    private ElementType elementType;

    protected MyElement parentElement;
    protected final List<MyElement> subElements = new ArrayList<>();

    public MyElement(
        MyElement parentElement,
        String id,
        String name,
        String description
    ) {
        this.parentElement = parentElement;
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentElement.addSubElement(this);
    }

    public MyElement(String name) {
        this.name = name;
    }

    public MyElement(String id, String name) {
        this.id = id;
        this.name = name;
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

    public ElementType getElementType() {
        return elementType;
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
}
