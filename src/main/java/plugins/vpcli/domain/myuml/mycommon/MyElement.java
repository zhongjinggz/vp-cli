package plugins.vpcli.domain.myuml.mycommon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyElement {
    private String id = "";
    private String name = "";
    private String elementType = "";
    private String description = "";
    private MyElement parent;
    private final List<MyElement> children = new ArrayList<>();
    private final List<MyReference> references = new ArrayList<>();

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

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MyElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(MyElement element) {
        element.parent = this;
        this.children.add(element);
    }

    public void addChildren(List<? extends MyElement> elements) {
        if (elements != null) {
            for (var element : elements) {
               addChild(element);
            }
        }
    }

    public List<MyReference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public void addReference(MyReference myReference) {
        this.references.add(myReference);
    }

}
