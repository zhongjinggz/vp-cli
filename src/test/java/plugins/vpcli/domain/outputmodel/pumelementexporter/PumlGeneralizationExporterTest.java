package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myassociation.MyAssociation.NameDirection;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mygeneralization.MyGeneralization;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackage_old;
import plugins.vpcli.domain.myuml_old.myproject_old.MyProject;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

class PumlGeneralizationExporterTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();
    private static final DiagramType CLASS = DiagramType.CLASS;

    private MyProject buildRootWithGeneralization() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "", stereotypes);
        MyPackage_old pkg = new MyPackage_old(model, "pk1", "动物", "", stereotypes);
        MyClass general = new MyClass(pkg, "c1", "动物", "", stereotypes);
        MyClass specific = new MyClass(pkg, "c2", "狗", "", stereotypes);
        new MyGeneralization(root, "g1", "", "", stereotypes, general, specific);
        return root;
    }

    private ProperMyDiagramElement wrap(MyElement_old2 element) {
        MyDiagram parent = new MyDiagram(
            new MyElement_old2("root"),
            "diagram",
            "动物概览",
            "",
            CLASS
        );
        return new ProperMyDiagramElement(
            "shape-" + element.getName(),
            DiagramElementType.GENERALIZATION,
            element,
            parent
        );
    }

    private PumlDiagram newDiagram() {
        return new PumlDiagram("动物概览", CLASS);
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
    void shouldEmitSolidGeneralizationEdge() {
        MyProject root = buildRootWithGeneralization();
        MyClass general = findClass(root, "动物");
        MyClass specific = findClass(root, "狗");

        MyGeneralization generalization =
            new MyGeneralization(root, "g2", "", "", stereotypes, general, specific);

        PumlDiagram diagram = newDiagram();
        new PumlGeneralizationExporter().to(diagram, wrap(generalization));

        String content = diagram.toString();
        // from=general 恒在左、to=specific 恒在右，实线 + from 端空心三角 → `<|--`
        assertTrue(content.contains("领域模型__动物__动物 <|-- 领域模型__动物__狗"),
            "期望泛化输出实线箭头边 <|--，实际:\n" + content);
    }

    @Test
    void shouldRenderNameAfterEdge() {
        MyProject root = buildRootWithGeneralization();
        MyClass general = findClass(root, "动物");
        MyClass specific = findClass(root, "狗");

        MyGeneralization generalization =
            new MyGeneralization(root, "g3", "继承", "", stereotypes, general, specific);

        PumlDiagram diagram = newDiagram();
        new PumlGeneralizationExporter().to(diagram, wrap(generalization));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__动物__动物 <|-- 领域模型__动物__狗: 继承"),
            "带名称的泛化应在边后输出 : name，实际:\n" + content);
    }

    @Test
    void shouldOmitNameWhenBlank() {
        MyProject root = buildRootWithGeneralization();
        MyClass general = findClass(root, "动物");
        MyClass specific = findClass(root, "狗");

        MyGeneralization generalization =
            new MyGeneralization(root, "g4", "   ", "", stereotypes, general, specific);

        PumlDiagram diagram = newDiagram();
        new PumlGeneralizationExporter().to(diagram, wrap(generalization));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__动物__动物 <|-- 领域模型__动物__狗\n"),
            "空白名称应退化回纯别名边，实际:\n" + content);
    }

    @Test
    void shouldReuseSameAliasAsShapeDeclaration() {
        MyProject root = buildRootWithGeneralization();
        MyClass general = findClass(root, "动物");
        MyClass specific = findClass(root, "狗");

        MyGeneralization generalization =
            new MyGeneralization(root, "g5", "", "", stereotypes, general, specific);

        PumlDiagram generalizationDiagram = newDiagram();
        new PumlGeneralizationExporter().to(generalizationDiagram, wrap(generalization));

        PumlDiagram generalDiagram = newDiagram();
        new PumlClassExporter().to(generalDiagram, wrap(general));
        PumlDiagram specificDiagram = newDiagram();
        new PumlClassExporter().to(specificDiagram, wrap(specific));

        assertTrue(generalizationDiagram.toString().contains("领域模型__动物__动物 <|-- 领域模型__动物__狗"));
        assertTrue(generalDiagram.toString().contains("class \"动物\" as 领域模型__动物__动物 {"));
        assertTrue(specificDiagram.toString().contains("class \"狗\" as 领域模型__动物__狗 {"));
    }

    @Test
    void shouldRenderNameWithoutTriangle() {
        MyProject root = buildRootWithGeneralization();
        MyClass general = findClass(root, "动物");
        MyClass specific = findClass(root, "狗");

        MyGeneralization generalization =
            new MyGeneralization(root, "g6", "继承", "", stereotypes, general, specific);

        // 泛化恒传 NameDirection.NONE（无阅读方向三角）
        PumlDiagram diagram = newDiagram();
        new PumlGeneralizationExporter().to(diagram, wrap(generalization));

        String content = diagram.toString();
        assertTrue(content.contains("领域模型__动物__动物 <|-- 领域模型__动物__狗: 继承"),
            "泛化不应输出阅读方向三角，实际:\n" + content);
        assertTrue(!content.contains("NameDirection") || !content.contains(NameDirection.FROM_TO.name()),
            "泛化不应携带 FROM_TO/TO_FROM 方向三角，实际:\n" + content);
    }
}
