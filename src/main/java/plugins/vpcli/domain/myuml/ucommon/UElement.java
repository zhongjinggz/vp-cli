package plugins.vpcli.domain.myuml.ucommon;

import java.util.List;

public class UElement {
    private String id;
    private String name;
    private String elementType;
    private String description;
    private List<UElement> children;
    private List<UReference> references;

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
        return children;
    }

    public void setChildren(List<UElement> children) {
        this.children = children;
    }

    public List<UReference> getReferences() {
        return references;
    }

    public void setReferences(List<UReference> references) {
        this.references = references;
    }

}
