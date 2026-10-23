package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationEnd;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mycommon.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

class PumlAssociationExporterTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();
    private static final DiagramType CLASS = DiagramType.CLASS;

    private MyProject buildRootWithAssociation() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "", stereotypes);
        MyPackage pkg = new MyPackage(model, "pk1", "订单", "", stereotypes);
        MyClass order = new MyClass(pkg, "c1", "订单", "", stereotypes);
        MyClass customer = new MyClass(pkg, "c2", "客户", "", stereotypes);
        new MyAssociation(root, "a1", "", "", stereotypes,
            new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "1",
                MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
            new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "0..*",
                MyAssociationEnd.NAVIGABLE_UNSPECIFIED));
        return root;
    }

    private ProperMyDiagramElement wrap(MyElement element) {
        MyDiagram parent = new MyDiagram(
            new MyElement("root"),
            "diagram",
            "订单概览",
            "",
            CLASS
        );
        return new ProperMyDiagramElement(
            "shape-" + element.getName(),
            DiagramElementType.ASSOCIATION,
            element,
            parent
        );
    }

    private PumlDiagram newDiagram() {
        return new PumlDiagram("订单概览", CLASS);
    }

    private MyClass findClass(MyProject root, String name) {
        for (MyElement e : root.getSubElements()) {
            if (e instanceof MyModel) {
                for (MyElement s : e.getSubElements()) {
                    if (s instanceof MyPackage) {
                        for (MyElement c : s.getSubElements()) {
                            if (c instanceof MyClass && name.equals(c.getName())) {
                                return (MyClass) c;
                            }
                        }
                    }
                }
            }
        }
        throw new AssertionError("no class: " + name);
    }

    @Test
    void shouldEmitAliasLine() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");
        MyAssociation association =
            new MyAssociation(root, "a2", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "1",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "0..*",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        // 订单包下两个类，全限定名：领域模型.订单.订单 与 领域模型.订单.客户
        assertTrue(content.contains("领域模型__订单__订单 \"1\" -- \"0..*\" 领域模型__订单__客户"),
            "期望输出带多重性的关联行，实际:\n" + content);
    }

    @Test
    void shouldOmitMultiplicityWhenEmpty() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a4", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 -- 领域模型__订单__客户"),
            "无多重性时应输出纯别名行，实际:\n" + content);
    }

    @Test
    void shouldReuseSameAliasAsShapeDeclaration() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        // 关联线以 PumlAssociationExporter 生成
        MyAssociation association =
            new MyAssociation(root, "a3", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));
        PumlDiagram associationDiagram = newDiagram();
        new PumlAssociationExporter().to(associationDiagram, wrap(association));

        // 两个端点类各自以 PumlClassExporter 生成 shape 声明别名
        PumlDiagram orderDiagram = newDiagram();
        new PumlClassExporter().to(orderDiagram, wrap(order));
        PumlDiagram customerDiagram = newDiagram();
        new PumlClassExporter().to(customerDiagram, wrap(customer));

        assertTrue(associationDiagram.toString().contains("领域模型__订单__订单 -- 领域模型__订单__客户"));
        assertTrue(orderDiagram.toString().contains("class \"订单\" as 领域模型__订单__订单 {"));
        assertTrue(customerDiagram.toString().contains("class \"客户\" as 领域模型__订单__客户 {"));
    }

    @Test
    void shouldRenderRoleNames() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        // 两端都带角色名，无多重性 → 每端一个引号标签
        MyAssociation association =
            new MyAssociation(root, "a9", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "订单端", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "客户端", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 \"订单端\" -- \"客户端\" 领域模型__订单__客户"),
            "只带角色名时应各渲染一个引号标签，实际:\n" + content);
    }

    @Test
    void shouldMergeRoleAndMultiplicityIntoOneLabel() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        // 角色名 + 多重性并存 → 合并进同一引号：role 在前、multiplicity 在后
        MyAssociation association =
            new MyAssociation(root, "a10", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "订单端", "", stereotypes, order, "1",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "客户端", "", stereotypes, customer, "0..*",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 \"订单端 1\" -- \"客户端 0..*\" 领域模型__订单__客户"),
            "角色与多重性应合并为单一引号标签，实际:\n" + content);
    }

    @Test
    void shouldEmitArrowOnToEndOnlyWhenFromNonNavigableUnspecifiedAndToNavigable() {
        // from 端 Unspecified、to 端可导航 → 仅 to 端（右侧）画箭头，渲染成 `-->`
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a5", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 --> 领域模型__订单__客户"),
            "期望 to 端箭头，实际:\n" + content);
    }

    @Test
    void shouldEmitBidirectionalArrowsWhenBothNavigable() {
        // 两端均可导航 → 两端各画箭头 `<-->`
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a6", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 <--> 领域模型__订单__客户"),
            "期望双向箭头，实际:\n" + content);
    }

    @Test
    void shouldRenderCrossOnNonNavigableEnd() {
        // from 端不可导航、to 端可导航 → from 端（左侧）叉号 `x-->`
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a7", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 x--> 领域模型__订单__客户"),
            "期望 from 端叉号、to 端箭头，实际:\n" + content);
    }

    @Test
    void shouldRenderCrossOnBothEndsWhenBothNonNavigable() {
        // 两端都不可导航 → 两端都叉号 `x--x`
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a8", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 x--x 领域模型__订单__客户"),
            "期望两端叉号，实际:\n" + content);
    }

    @Test
    void shouldRenderSharedDiamondOnToEndAndSuppressArrow() {
        // to 端 shared 聚合 → 空心菱形 `o`，即使 to 端可导航也不画箭头
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a11", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE, MyAssociationEnd.AGGREGATION_NONE),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE, MyAssociationEnd.AGGREGATION_SHARED));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 <--o 领域模型__订单__客户"),
            "shared 聚合应画空心菱形并抑制 to 端箭头，实际:\n" + content);
    }

    @Test
    void shouldRenderCompositeDiamondOnFromEndAndSuppressArrow() {
        // from 端 composite 组合 → 实心菱形 `*`，即使可导航也不画箭头
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a12", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE, MyAssociationEnd.AGGREGATION_COMPOSITE),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE, MyAssociationEnd.AGGREGATION_NONE));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 *--> 领域模型__订单__客户"),
            "composite 聚合应画实心菱形并抑制 from 端箭头，实际:\n" + content);
    }

    @Test
    void shouldRenderSharedDiamondOnLeftEnd() {
        // from 端 shared → 左端空心菱形 `o--`
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a13", "", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_NON_NAVIGABLE, MyAssociationEnd.AGGREGATION_SHARED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_NAVIGABLE, MyAssociationEnd.AGGREGATION_NONE));

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 o--> 领域模型__订单__客户"),
            "from 端 shared 应画左端空心菱形，实际:\n" + content);
    }

    // ---- 关联名称及阅读方向（#9） ----

    @Test
    void shouldRenderNameWithoutDirection() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a14", "doSomething", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));
        association.setNameDirection(MyAssociation.NameDirection.NONE);

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 -- 领域模型__订单__客户: doSomething"),
            "带名称且 NONE 方向应只追加 : name 不画三角，实际:\n" + content);
    }

    @Test
    void shouldRenderNameWithFromToArrow() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a15", "doSomething", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));
        association.setNameDirection(MyAssociation.NameDirection.FROM_TO);

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        // 阅读从左到右 → 三角在名称右侧：`doSomething>`
        assertTrue(content.contains(": doSomething >"),
            "FROM_TO 应在名称右侧画三角，实际:\n" + content);
    }

    @Test
    void shouldRenderNameWithToFromArrow() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyAssociation association =
            new MyAssociation(root, "a16", "doSomething", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));
        association.setNameDirection(MyAssociation.NameDirection.TO_FROM);

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        // 阅读从右到左 → 三角在名称左侧：`<doSomething`
        assertTrue(content.contains(": < doSomething"),
            "TO_FROM 应在名称左侧画三角，实际:\n" + content);
    }

    @Test
    void shouldOmitNameWhenBlankEvenWithDirection() {
        MyProject root = buildRootWithAssociation();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        // 名称为纯空白 + 方向 FROM_TO → 不应输出 : name 也不画三角
        MyAssociation association =
            new MyAssociation(root, "a17", "   ", "", stereotypes,
                new MyAssociationEnd(root, "fe", "", "", stereotypes, order, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED),
                new MyAssociationEnd(root, "te", "", "", stereotypes, customer, "",
                    MyAssociationEnd.NAVIGABLE_UNSPECIFIED));
        association.setNameDirection(MyAssociation.NameDirection.FROM_TO);

        PumlDiagram diagram = newDiagram();
        new PumlAssociationExporter().to(diagram, wrap(association));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 -- 领域模型__订单__客户\n"),
            "空白名称应退化回纯别名行，实际:\n" + content);
    }
}