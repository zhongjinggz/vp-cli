package plugins.vpcli.domain.myuml.mydependency;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

/** 依赖关系：UML 中一条有向关系，从 from 指向 to（对应 VP 的 IDependency）。 */
public class MyDependency extends MyElement {

    private MyElement from;
    private MyElement to;

    public MyDependency(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement from,
        MyElement to
    ) {
        super(parent, id, name, MyElementType.DEPENDENCY, description, stereotypes);
        this.from = from;
        this.to = to;
    }

    public MyElement getFrom() {
        return from;
    }

    public void setFrom(MyElement from) {
        this.from = from;
    }

    public MyElement getTo() {
        return to;
    }

    public void setTo(MyElement to) {
        this.to = to;
    }
}
