package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

public class MyAssociation extends MyElement_old2 {

    /** 关联名称的阅读方向（对应 UML 关联名称旁的小实心三角）。 */
    public enum NameDirection {
        FROM_TO,
        TO_FROM,
        /** 仅用作“无名称”时的占位，不输出方向三角。 */
        NONE
    }

    private MyAssociationEnd fromEnd;
    private MyAssociationEnd toEnd;
    private NameDirection nameDirection = NameDirection.NONE;

    public MyAssociation(
        MyElement_old2 parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyAssociationEnd fromEnd,
        MyAssociationEnd toEnd
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

    public MyAssociationEnd getFromEnd() {
        return fromEnd;
    }

    public void setFromEnd(MyAssociationEnd fromEnd) {
        this.fromEnd = fromEnd;
    }

    public MyAssociationEnd getToEnd() {
        return toEnd;
    }

    public void setToEnd(MyAssociationEnd toEnd) {
        this.toEnd = toEnd;
    }

    /** 兼容访问器：委托到端点的 element。 */
    public MyElement_old2 getFromElement() {
        return fromEnd == null ? null : fromEnd.getElement();
    }

    public void setFromElement(MyElement_old2 fromElement) {
        if (fromEnd == null) {
            fromEnd = new MyAssociationEnd(this, "", "", "", List.of(), fromElement, "",
                MyAssociationEnd.NAVIGABLE_UNSPECIFIED, MyAssociationEnd.AGGREGATION_NONE);
        }
        fromEnd.setElement(fromElement);
    }

    /** 兼容访问器：委托到端点的 element。 */
    public MyElement_old2 getToElement() {
        return toEnd == null ? null : toEnd.getElement();
    }

    public void setToElement(MyElement_old2 toElement) {
        if (toEnd == null) {
            toEnd = new MyAssociationEnd(this, "", "", "", List.of(), toElement, "",
                MyAssociationEnd.NAVIGABLE_UNSPECIFIED, MyAssociationEnd.AGGREGATION_NONE);
        }
        toEnd.setElement(toElement);
    }
}
