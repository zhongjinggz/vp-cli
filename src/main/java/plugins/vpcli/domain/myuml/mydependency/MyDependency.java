package plugins.vpcli.domain.myuml.mydependency;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

/** 依赖关系：UML 中一条有向关系，从 from 指向 to（对应 VP 的 IDependency）。 */
public class MyDependency extends MyElement_old2 {

    private MyElement_old2 from;
    private MyElement_old2 to;

    public MyDependency(
        MyElement_old2 parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement_old2 from,
        MyElement_old2 to
    ) {
        super(parent, id, name, MyElementType.DEPENDENCY, description, stereotypes);
        this.from = from;
        this.to = to;
    }

    public MyElement_old2 getFrom() {
        return from;
    }

    public void setFrom(MyElement_old2 from) {
        this.from = from;
    }

    public MyElement_old2 getTo() {
        return to;
    }

    public void setTo(MyElement_old2 to) {
        this.to = to;
    }
}
