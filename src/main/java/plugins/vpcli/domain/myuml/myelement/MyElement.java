package plugins.vpcli.domain.myuml.myelement;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MyElement {
    private String id = "";
    private String name;
    private String description = "";
    private final List<MyStereotype> stereotypes = new ArrayList<>();
    private MyElementType myElementType;

    protected MyElement parentElement;
    protected final List<MyElement> subElements = new ArrayList<>();

    // for new elements
    public MyElement(
        MyElement parentElement,
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
    public MyElement(String name) {
        this.name = name;
    }

    //for MyProject
    public MyElement(String id, String name, MyElementType myElementType) {
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

    private static void collectQualifiedName(StringBuilder sb, MyElement element) {
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
