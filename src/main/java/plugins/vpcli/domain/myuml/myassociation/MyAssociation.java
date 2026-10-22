package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml.mycommon.MyElementType;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;

import java.util.List;

public class MyAssociation extends MyElement {

    private MyElement fromElement;
    private MyElement toElement;

    public MyAssociation(
        MyElement parent,
        String id,
        String name,
        String Description,
        List<MyStereotype> stereotypes) {
        super(parent,
            id,
            name,
            MyElementType.ASSOCIATION,
            Description,
            stereotypes
        );
    }

    public MyAssociation(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement fromElement,
        MyElement toElement
    ) {
        super(parent, id, name, MyElementType.ASSOCIATION, description, stereotypes);
        this.fromElement = fromElement;
        this.toElement = toElement;
    }

    public MyElement getFromElement() {
        return fromElement;
    }

    public void setFromElement(MyElement fromElement) {
        this.fromElement = fromElement;
    }

    public MyElement getToElement() {
        return toElement;
    }

    public void setToElement(MyElement toElement) {
        this.toElement = toElement;
    }
}
