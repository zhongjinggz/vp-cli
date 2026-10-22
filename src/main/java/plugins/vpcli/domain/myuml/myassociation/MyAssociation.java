package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

public class MyAssociation extends MyElement {

    private MyAssociationEnd fromEnd;
    private MyAssociationEnd toEnd;

    public MyAssociation(
        MyElement parent,
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
    public MyElement getFromElement() {
        return fromEnd == null ? null : fromEnd.getElement();
    }

    public void setFromElement(MyElement fromElement) {
        if (fromEnd == null) {
            fromEnd = new MyAssociationEnd(this, "", "", "", List.of(), fromElement, "",
                MyAssociationEnd.NAVIGABLE_UNSPECIFIED);
        }
        fromEnd.setElement(fromElement);
    }

    /** 兼容访问器：委托到端点的 element。 */
    public MyElement getToElement() {
        return toEnd == null ? null : toEnd.getElement();
    }

    public void setToElement(MyElement toElement) {
        if (toEnd == null) {
            toEnd = new MyAssociationEnd(this, "", "", "", List.of(), toElement, "",
                MyAssociationEnd.NAVIGABLE_UNSPECIFIED);
        }
        toEnd.setElement(toElement);
    }
}
