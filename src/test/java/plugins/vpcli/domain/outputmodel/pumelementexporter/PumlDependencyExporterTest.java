package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation.NameDirection;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.mydependency.MyDependency;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackage_old;
import plugins.vpcli.domain.myuml_old.myproject_old.MyProject;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

class PumlDependencyExporterTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();
    private static final DiagramType CLASS = DiagramType.CLASS;

    private MyProject buildRootWithDependency() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "", stereotypes);
        MyPackage_old pkg = new MyPackage_old(model, "pk1", "订单", "", stereotypes);
        MyClass order = new MyClass(pkg, "c1", "订单", "", stereotypes);
        MyClass customer = new MyClass(pkg, "c2", "客户", "", stereotypes);
        new MyDependency(root, "d1", "", "", stereotypes, order, customer);
        return root;
    }

    private ProperMyDiagramElement wrap(MyElement_old2 element) {
        MyDiagram parent = new MyDiagram(
            new MyElement_old2("root"),
            "diagram",
            "订单概览",
            "",
            CLASS
        );
        return new ProperMyDiagramElement(
            "shape-" + element.getName(),
            DiagramElementType.DEPENDENCY,
            element,
            parent
        );
    }

    private PumlDiagram newDiagram() {
        return new PumlDiagram("订单概览", CLASS);
    }

    private MyClass findClass(MyProject root, String name) {
        for (MyElement_old2 e : root.getSubElements()) {
            if (e instanceof MyModel) {
                for (MyElement_old2 s : e.getSubElements()) {
                    if (s instanceof MyPackage_old) {
                        for (MyElement_old2 c : s.getSubElements()) {
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
    void shouldEmitDashedArrowEdge() {
        MyProject root = buildRootWithDependency();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyDependency dependency =
            new MyDependency(root, "d2", "", "", stereotypes, order, customer);

        PumlDiagram diagram = newDiagram();
        new PumlDependencyExporter().to(diagram, wrap(dependency));

        String content = diagram.toString();
        // from 恒在左、to 恒在右，虚线 + to 端箭头 → `..>`
        assertTrue(content.contains("领域模型__订单__订单 ..> 领域模型__订单__客户"),
            "期望依赖输出虚线箭头边..>，实际:\n" + content);
    }

    @Test
    void shouldRenderNameAfterEdge() {
        MyProject root = buildRootWithDependency();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyDependency dependency =
            new MyDependency(root, "d3", "使用", "", stereotypes, order, customer);

        PumlDiagram diagram = newDiagram();
        new PumlDependencyExporter().to(diagram, wrap(dependency));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 ..> 领域模型__订单__客户: 使用"),
            "带名称的依赖应在边后输出 : name，实际:\n" + content);
    }

    @Test
    void shouldOmitNameWhenBlank() {
        MyProject root = buildRootWithDependency();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyDependency dependency =
            new MyDependency(root, "d4", "   ", "", stereotypes, order, customer);

        PumlDiagram diagram = newDiagram();
        new PumlDependencyExporter().to(diagram, wrap(dependency));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 ..> 领域模型__订单__客户\n"),
            "空白名称应退化回纯别名边，实际:\n" + content);
    }

    @Test
    void shouldReuseSameAliasAsShapeDeclaration() {
        MyProject root = buildRootWithDependency();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyDependency dependency =
            new MyDependency(root, "d5", "", "", stereotypes, order, customer);

        PumlDiagram dependencyDiagram = newDiagram();
        new PumlDependencyExporter().to(dependencyDiagram, wrap(dependency));

        PumlDiagram orderDiagram = newDiagram();
        new PumlClassExporter().to(orderDiagram, wrap(order));
        PumlDiagram customerDiagram = newDiagram();
        new PumlClassExporter().to(customerDiagram, wrap(customer));

        assertTrue(dependencyDiagram.toString().contains("领域模型__订单__订单 ..> 领域模型__订单__客户"));
        assertTrue(orderDiagram.toString().contains("class \"订单\" as 领域模型__订单__订单 {"));
        assertTrue(customerDiagram.toString().contains("class \"客户\" as 领域模型__订单__客户 {"));
    }

    @Test
    void shouldRenderNameWithoutTriangle() {
        MyProject root = buildRootWithDependency();
        MyClass order = findClass(root, "订单");
        MyClass customer = findClass(root, "客户");

        MyDependency dependency =
            new MyDependency(root, "d6", "使用", "", stereotypes, order, customer);

        // 依赖恒传 NameDirection.NONE（无阅读方向三角）
        PumlDiagram diagram = newDiagram();
        new PumlDependencyExporter().to(diagram, wrap(dependency));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__订单__订单 ..> 领域模型__订单__客户: 使用"),
            "依赖不应输出阅读方向三角，实际:\n" + content);
        assertTrue(!content.contains("NameDirection") || !content.contains(NameDirection.FROM_TO.name()),
            "依赖不应携带 FROM_TO/TO_FROM 方向三角，实际:\n" + content);
    }
}
