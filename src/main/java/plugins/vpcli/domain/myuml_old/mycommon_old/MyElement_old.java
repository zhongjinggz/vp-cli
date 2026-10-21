package plugins.vpcli.domain.myuml_old.mycommon_old;

import plugins.vpcli.domain.myuml.mycommon.MyReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyElement_old {
    private String id = "";
    private String name = "";
    private String alias = "";
    private String description = "";
    private String elementType = "";

    protected MyElement_old parent;
    protected final List<MyElement_old> children = new ArrayList<>();

    public MyElement_old(String id, String name, String description) {
       this.id = id;
       this.name = name;
       this.description = description;
    }

    public MyElement_old(MyElement_old parent, String id, String name, String description) {
       this.parent = parent;
       this.id = id;
       this.name = name;
       this.description = description;
       this.parent.addChild(this);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    private final List<MyReference> references = new ArrayList<>();

    public MyElement_old(String name) {
        this.name = name;
    }

    public MyElement_old(String id, String name) {
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

    public List<MyElement_old> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(MyElement_old element) {
        element.parent = this;
        this.children.add(element);
    }

    public void addChildren(List<? extends MyElement_old> elements) {
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

    public MyElement_old getParent() {
        return parent ;
    }
}
