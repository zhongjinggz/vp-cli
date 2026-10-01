package plugins.vpcli.domain.myuml.ucommon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UElement {
    private String id = "";
    private String name = "";
    private String elementType = "";
    private String description = "";
    private UElement parent;
    private final List<UElement> children = new ArrayList<>();
    private final List<UReference> references = new ArrayList<>();

    public UElement(String name) {
        this.name = name;
    }

    public UElement(String id, String name) {
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

    public List<UElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(UElement element) {
        element.parent = this;
        this.children.add(element);
    }

    public void addChildren(List<? extends UElement> elements) {
        if (elements != null) {
            for (var element : elements) {
               addChild(element);
            }
        }
    }

    public List<UReference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public void addReference(UReference uReference) {
        this.references.add(uReference);
    }

}
