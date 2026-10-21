package plugins.vpcli.domain.myuml.mycommon;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MyElement {
    private String id = "";
    private String name ;
    private String description = "";
    private final List<String> stereotypes = new ArrayList<>();
    private ElementType elementType;

    protected MyElement parentElement;
    protected final List<MyElement> subElements = new ArrayList<>();

    // for new elements
    public MyElement(
        MyElement parentElement,
        String id,
        String name,
        ElementType elementType,
        String description
    ) {
        this.parentElement = parentElement;
        this.id = id;
        this.name = name;
        this.description = description;
        this.elementType = elementType;
        this.parentElement.addSubElement(this);
    }

    // old
    public MyElement(String name) {
        this.name = name;
    }

    //for MyProject
    public MyElement(String id, String name, ElementType elementType) {
        this.id = id;
        this.name = name;
//        this.myElementType = myElementType;
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

    public ElementType getElementType() {
        return elementType;
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

    private static void collectQualifiedName(StringBuilder sb, MyElement element) {
        if (element == null) {
            return;
        }
        collectQualifiedName(sb, element.parentElement);
        if (element.elementType != ElementType.PROJECT) {
            if (element.name != null && !element.name.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append('.');
                }
                sb.append(element.name);
            }
        }
    }
}
