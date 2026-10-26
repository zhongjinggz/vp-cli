package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

public class MyAssociation extends MyElement {

    /** 关联名称的阅读方向（对应 UML 关联名称旁的小实心三角）。 */
    public enum NameDirection {
        FROM_TO,
        TO_FROM,
        NONE   // 仅用作“无名称”时的占位，不输出方向三角。
    }

    private MyProperty fromEnd;
    private MyProperty toEnd;
    private NameDirection nameDirection = NameDirection.NONE;

    public MyAssociation(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyProperty fromEnd,
        MyProperty toEnd
    ) {
        super(parent, id, name, MyElementType.ASSOCIATION, description, stereotypes);
        this.fromEnd = fromEnd;
        this.toEnd = toEnd;
    }

    public NameDirection getNameDirection() {
        return nameDirection;
    }

    public void setNameDirection(NameDirection nameDirection) {
        this.nameDirection = nameDirection == null ? NameDirection.NONE : nameDirection;
    }

    public MyProperty getFromEnd() {
        return fromEnd;
    }

    public void setFromEnd(MyProperty fromEnd) {
        this.fromEnd = fromEnd;
    }

    public MyProperty getToEnd() {
        return toEnd;
    }

    public void setToEnd(MyProperty toEnd) {
        this.toEnd = toEnd;
    }

    /** 兼容访问器：委托到端点的 element。 */
    public MyElement getFromElement() {
        return fromEnd == null ? null : fromEnd.getElement();
    }

    public void setFromElement(MyElement fromElement) {
        if (fromEnd == null) {
            fromEnd = new MyProperty(this, "", "", "", List.of(), fromElement, "",
                MyProperty.NAVIGABLE_UNSPECIFIED, MyProperty.AGGREGATION_NONE);
        }
        fromEnd.setElement(fromElement);
    }

    /** 兼容访问器：委托到端点的 element。 */
    public MyElement getToElement() {
        return toEnd == null ? null : toEnd.getElement();
    }

    public void setToElement(MyElement toElement) {
        if (toEnd == null) {
            toEnd = new MyProperty(this, "", "", "", List.of(), toElement, "",
                MyProperty.NAVIGABLE_UNSPECIFIED, MyProperty.AGGREGATION_NONE);
        }
        toEnd.setElement(toElement);
    }
}
