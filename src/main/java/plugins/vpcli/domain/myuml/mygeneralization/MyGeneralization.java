package plugins.vpcli.domain.myuml.mygeneralization;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;

import java.util.List;

/**
 * 泛化（继承）关系：UML 中从子类（specific）指向父类（general）。
 * 对应 VP 的 IGeneralization。字段用 UML 规范术语 general（父类）/ specific（子类），
 * 避免与 VP API 的 parent/child 语义混淆。
 */
public class MyGeneralization extends MyElement_old2 {

    private MyElement_old2 general;
    private MyElement_old2 specific;

    public MyGeneralization(
        MyElement_old2 parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement_old2 general,
        MyElement_old2 specific
    ) {
        super(parent, id, name, MyElementType.GENERALIZATION, description, stereotypes);
        this.general = general;
        this.specific = specific;
    }

    public MyElement_old2 getGeneral() {
        return general;
    }

    public void setGeneral(MyElement_old2 general) {
        this.general = general;
    }

    public MyElement_old2 getSpecific() {
        return specific;
    }

    public void setSpecific(MyElement_old2 specific) {
        this.specific = specific;
    }
}
