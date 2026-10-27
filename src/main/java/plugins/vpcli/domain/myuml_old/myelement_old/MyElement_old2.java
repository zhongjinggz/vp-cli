package plugins.vpcli.domain.myuml_old.myelement_old;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MyElement_old2 {
    private String id = "";
    private String name;
    private String description = "";
    private final List<MyStereotype> stereotypes = new ArrayList<>();
    private MyElementType myElementType;

    protected MyElement_old2 parentElement;
    protected final List<MyElement_old2> subElements = new ArrayList<>();

    // for new elements
    public MyElement_old2(
        MyElement_old2 parentElement,
        String id,
        String name,
        MyElementType myElementType,
        String description,
        List<MyStereotype> stereotypes
    ) {
        this.parentElement = parentElement;
        this.id = id;
        this.name = name;
        this.description = description;
        this.myElementType = myElementType;
        this.parentElement.addSubElement(this);
        this.stereotypes.addAll(stereotypes);
    }

    // old
    public MyElement_old2(String name) {
        this.name = name;
    }

    //for MyProject
    public MyElement_old2(String id, String name, MyElementType myElementType) {
        this.id = id;
        this.name = name;
        this.myElementType = myElementType;
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

    public String getDescription() {
        return description;
    }

    public List<MyElement_old2> getSubElements() {
        return Collections.unmodifiableList(subElements);
    }

    public void addSubElement(MyElement_old2 subElement) {
        this.subElements.add(subElement);
    }

    public void addSubElements(@NotNull List<? extends MyElement_old2> elements) {
        Objects.requireNonNull(elements, "Bug: elements can't be null");
        for (var element : elements) {
            addSubElement(element);
        }
    }

    public MyElementType getElementType() {
        return myElementType;
    }

    public List<MyStereotype> getStereotypes() {
        return Collections.unmodifiableList(stereotypes);
    }

    /**
     * 全限定名：沿 parentElement 链收集各层名字，自上而下以 "." 连接。
     * 跳过 PROJECT 根节点，因此顶层元素（如直接挂在 Project/Model 下的类、用例）
     * 只包含自身名称。
     */
    public String getQualifiedName() {
        StringBuilder sb = new StringBuilder();
        collectQualifiedName(sb, this);
        return sb.toString();
    }

    public void addStereotype(MyStereotype stereotype) {
        this.stereotypes.add(stereotype);
    }

    private static void collectQualifiedName(StringBuilder sb, MyElement_old2 element) {
        if (element == null) {
            return;
        }
        collectQualifiedName(sb, element.parentElement);
        if (element.myElementType != MyElementType.PROJECT) {
            if (element.name != null && !element.name.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append('.');
                }
                sb.append(element.name);
            }
        }
    }
}
