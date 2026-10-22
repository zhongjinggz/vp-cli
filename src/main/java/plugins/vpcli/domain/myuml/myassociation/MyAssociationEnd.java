package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

/**
 * 关联的一端：封装该端关联到的端点元素 {@link #element}，
 * 以及该端的多重性 {@link #multiplicity}（与 VP 的 IAssociationEnd 对应）。
 */
public class MyAssociationEnd extends MyElement {

    /** 领域层导航性常量，值与 VP SDK 的 IAssociationEnd 常量一致（直接透传）。 */
    public static final int NAVIGABLE_NAVIGABLE = 0;
    public static final int NAVIGABLE_UNSPECIFIED = 1;
    public static final int NAVIGABLE_NON_NAVIGABLE = 2;

    private MyElement element;
    private String multiplicity = "";
    private int navigable = NAVIGABLE_UNSPECIFIED;

    public MyAssociationEnd(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement element,
        String multiplicity,
        int navigable
    ) {
        super(parent, id, name, MyElementType.ASSOCIATION_END, description, stereotypes);
        this.element = element;
        setMultiplicity(multiplicity);
        this.navigable = navigable;
    }

    public MyElement getElement() {
        return element;
    }

    public void setElement(MyElement element) {
        this.element = element;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(String multiplicity) {
        // null 或 "Unspecified" 归一化为空串
        if (multiplicity == null || "Unspecified".equals(multiplicity)) {
            this.multiplicity = "";
        } else {
            this.multiplicity = multiplicity;
        }
    }

    public int getNavigable() {
        return navigable;
    }

    public void setNavigable(int navigable) {
        this.navigable = navigable;
    }
}
