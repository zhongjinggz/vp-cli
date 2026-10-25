package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myclassifier.MyAttribute;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyOperation;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.myelement.MyElement;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

class PumlClassExporterTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();
    private static final DiagramType CLASS = DiagramType.CLASS;

    private MyProject buildRootWithClass() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "", stereotypes);
        MyPackage pkg = new MyPackage(model, "pk1", "订单", "", stereotypes);
        new MyClass(pkg, "c1", "订单", "", stereotypes);
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
            DiagramElementType.CLASS,
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
    void shouldRenderAttributeLines() {
        MyProject root = buildRootWithClass();
        MyClass clazz = findClass(root, "订单");

        clazz.addAttribute(new MyAttribute("", "id", "", "", "instance"));
        clazz.addAttribute(new MyAttribute("public", "name", "string", "", "instance"));
        clazz.addAttribute(new MyAttribute("", "age", "int", "18", "instance"));
        clazz.addAttribute(new MyAttribute("", "", "status", "", "instance"));

        PumlDiagram diagram = newDiagram();
        new PumlClassExporter().to(diagram, wrap(clazz));

        String content = diagram.toString();
        assertTrue(content.contains("    id\n"), "仅名称属性，实际:\n" + content);
        assertTrue(content.contains("    + name: string\n"), "公有名+类型，实际:\n" + content);
        assertTrue(content.contains("    age: int = 18\n"), "名+类型+初值，实际:\n" + content);
        assertTrue(content.contains("    : status\n"), "仅类型，实际:\n" + content);
    }

    @Test
    void shouldRenderVisibilityVisibilitySymbols() {
        MyProject root = buildRootWithClass();
        MyClass clazz = findClass(root, "订单");

        clazz.addAttribute(new MyAttribute("public", "publicField", "int", "", "instance"));
        clazz.addAttribute(new MyAttribute("private", "privateField", "int", "", "instance"));
        clazz.addAttribute(new MyAttribute("protected", "protectedField", "int", "", "instance"));
        clazz.addAttribute(new MyAttribute("package", "packageField", "int", "", "instance"));

        PumlDiagram diagram = newDiagram();
        new PumlClassExporter().to(diagram, wrap(clazz));

        String content = diagram.toString();
        assertTrue(content.contains("    + publicField: int\n"), "公有+，实际:\n" + content);
        assertTrue(content.contains("    - privateField: int\n"), "私有-，实际:\n" + content);
        assertTrue(content.contains("    # protectedField: int\n"), "保护#，实际:\n" + content);
        assertTrue(content.contains("    ~ packageField: int\n"), "包级~，实际:\n" + content);
    }

    @Test
    void shouldRenderOperationLines() {
        MyProject root = buildRootWithClass();
        MyClass clazz = findClass(root, "订单");

        clazz.addOperation(new MyOperation("", "open", "void", false, new ArrayList<>(), "instance"));
        clazz.addOperation(new MyOperation("", "close", "void", false, new ArrayList<>(), "instance"));
        List<MyOperation.Parameter> logParams = new ArrayList<>();
        logParams.add(new MyOperation.Parameter("message", "string", ""));
        clazz.addOperation(new MyOperation("", "log", "void", false, logParams, "instance"));
        List<MyOperation.Parameter> saveParams = new ArrayList<>();
        saveParams.add(new MyOperation.Parameter("file", "string", ""));
        clazz.addOperation(new MyOperation("private", "save", "boolean", false, saveParams, "instance"));
        clazz.addOperation(new MyOperation("", "version", "string", false, new ArrayList<>(), "instance"));

        PumlDiagram diagram = newDiagram();
        new PumlClassExporter().to(diagram, wrap(clazz));

        String content = diagram.toString();
        assertTrue(content.contains("    open(): void\n"), "无参操作，实际:\n" + content);
        assertTrue(content.contains("    close(): void\n"), "无参操作2，实际:\n" + content);
        assertTrue(content.contains("    log(message: string): void\n"), "带参操作，实际:\n" + content);
        assertTrue(content.contains("    - save(file: string): boolean\n"), "私有+带参+返回，实际:\n" + content);
        assertTrue(content.contains("    version(): string\n"), "返回类型操作，实际:\n" + content);
    }

    @Test
    void shouldRenderAbstractAndStaticPrefixes() {
        MyProject root = buildRootWithClass();
        MyClass clazz = findClass(root, "订单");

        clazz.addOperation(new MyOperation("", "draw", "void", true, new ArrayList<>(), "instance"));
        clazz.addAttribute(new MyAttribute("", "count", "int", "0", "classifier"));

        PumlDiagram diagram = newDiagram();
        new PumlClassExporter().to(diagram, wrap(clazz));

        String content = diagram.toString();
        assertTrue(content.contains("    {abstract} draw(): void\n"), "抽象前缀，实际:\n" + content);
        assertTrue(content.contains("    {static} count: int = 0\n"), "静态前缀，实际:\n" + content);
    }
}
