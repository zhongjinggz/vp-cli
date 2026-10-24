package plugins.vpcli.domain.myuml.mygeneralization;

import plugins.vpcli.domain.myuml.myelement.MyElementType;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

/**
 * 泛化（继承）关系：UML 中从子类（specific）指向父类（general）。
 * 对应 VP 的 IGeneralization。字段用 UML 规范术语 general（父类）/ specific（子类），
 * 避免与 VP API 的 parent/child 语义混淆。
 */
public class MyGeneralization extends MyElement {

    private MyElement general;
    private MyElement specific;

    public MyGeneralization(
        MyElement parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement general,
        MyElement specific
    ) {
        super(parent, id, name, MyElementType.GENERALIZATION, description, stereotypes);
        this.general = general;
        this.specific = specific;
    }

    public MyElement getGeneral() {
        return general;
    }

    public void setGeneral(MyElement general) {
        this.general = general;
    }

    public MyElement getSpecific() {
        return specific;
    }

    public void setSpecific(MyElement specific) {
        this.specific = specific;
    }
}
