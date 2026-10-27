package plugins.vpcli.domain.myuml.myassociation;

import plugins.vpcli.domain.myuml_old.myelement_old.MyElementType;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import com.vp.plugin.model.IAssociationEnd;

import java.util.List;

/**
 * 关联的一端：封装该端关联到的端点元素 {@link #element}，
 * 以及该端的多重性 {@link #multiplicity}（与 VP 的 IAssociationEnd 对应）。
 */
public class MyAssociationEnd extends MyElement_old2 {

    /** 领域层导航性常量，值与 VP SDK 的 IAssociationEnd 常量一致（直接透传）。 */
    public static final int NAVIGABLE_NAVIGABLE = 0;
    public static final int NAVIGABLE_UNSPECIFIED = 1;
    public static final int NAVIGABLE_NON_NAVIGABLE = 2;

    /** 领域层聚合/组合常量，值与 VP SDK 的 IAssociationEnd 常量一致（直接透传）。 */
    public static final String AGGREGATION_NONE = IAssociationEnd.AGGREGATION_KIND_none;
    public static final String AGGREGATION_SHARED = IAssociationEnd.AGGREGATION_KIND_shared;
    public static final String AGGREGATION_COMPOSITE = IAssociationEnd.AGGREGATION_KIND_composite;

    private MyElement_old2 element;
    private String multiplicity = "";
    private int navigable = NAVIGABLE_UNSPECIFIED;
    private String aggregationKind = AGGREGATION_NONE;

    public MyAssociationEnd(
        MyElement_old2 parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement_old2 element,
        String multiplicity,
        int navigable,
        String aggregationKind
    ) {
        super(parent, id, name, MyElementType.ASSOCIATION_END, description, stereotypes);
        this.element = element;
        setMultiplicity(multiplicity);
        this.navigable = navigable;
        setAggregationKind(aggregationKind);
    }

    /** 便捷构造器：未提供聚合信息时，聚合/组合默认为 none。 */
    public MyAssociationEnd(
        MyElement_old2 parent,
        String id,
        String name,
        String description,
        List<MyStereotype> stereotypes,
        MyElement_old2 element,
        String multiplicity,
        int navigable
    ) {
        this(parent, id, name, description, stereotypes, element, multiplicity, navigable, AGGREGATION_NONE);
    }

    public MyElement_old2 getElement() {
        return element;
    }

    public void setElement(MyElement_old2 element) {
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

    public String getAggregationKind() {
        return aggregationKind;
    }

    public void setAggregationKind(String aggregationKind) {
        // null 归一化为 none
        this.aggregationKind = aggregationKind == null ? AGGREGATION_NONE : aggregationKind;
    }
}
