package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.mydiagram.DiagramType;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagram;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml_old.myelement_old.MyElement_old2;
import plugins.vpcli.domain.myuml.mystereotype.MyStereotype;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml_old.mypackage_old.MyPackage_old;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml_old.myusecase_old.MyUseCase_old;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

class PumlElementExporterTest {

    private static final List<MyStereotype> stereotypes = new ArrayList<>();
    private static final DiagramType CLASS = DiagramType.CLASS;

    // 构造根：Project 下挂 Model，Model 下挂订单包，验证全限定别名
    private MyProject buildRoot() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "", stereotypes);
        MyPackage_old orderPkg = new MyPackage_old(model, "pk1", "订单", "", stereotypes);
        new MyClass(orderPkg, "c1", "订单", "", stereotypes);
        return root;
    }

    private PumlDiagram newDiagram() {
        return new PumlDiagram("订单概览", CLASS);
    }

    // 用 ProperMyDiagramElement 包装 model 元素，父节点直接使用 MyDiagram（已实现 MyDiagramElement）
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
            plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.DiagramElementType.CLASS,
            element,
            parent
        );
    }

    @Test
    void shouldEmitClassWithAlias() {
        MyProject root = buildRoot();
        MyClass orderClass = findClass(root, "订单");

        PumlDiagram diagram = newDiagram();
        new PumlClassExporter().to(diagram, wrap(orderClass));

        assertTrue(diagram.toString().contains("class \"订单\" as 领域模型__订单__订单 {"));
    }

    @Test
    void shouldEmitPackageWithAlias() {
        MyProject root = buildRoot();
        MyPackage_old orderPkg = findPackage(root, "订单");

        PumlDiagram diagram = newDiagram();
        new PumlPackageExporter().to(diagram, wrap(orderPkg));

        assertTrue(diagram.toString().contains("namespace \"订单\" as 领域模型__订单 {"));
    }

    @Test
    void shouldEmitModelWithAliasAndStereotype() {
        MyProject root = buildRoot();
        MyModel model = findModel(root);

        PumlDiagram diagram = newDiagram();
        new PumlModelExporter().to(diagram, wrap(model));

        String content = diagram.toString();
        assertTrue(content.contains("namespace \"领域模型\" as 领域模型 <<model>> {"),
            "期望带别名与 <<model>> 原型，实际输出:\n" + content);
    }

    @Test
    void shouldEmitUseCaseWithAlias() {
        MyProject root = new MyProject("p", "我的项目");
        MyUseCase_old useCase = new MyUseCase_old(root, "u1", "增加用户", "", stereotypes);

        PumlDiagram diagram = newDiagram();
        new PumlUseCaseExporter().to(diagram, wrap(useCase));

        assertTrue(diagram.toString().contains("usecase \"增加用户\" as 增加用户"));
    }

    @Test
    void shouldKeepSimpleNameAsAliasWhenTopLevel() {
        MyProject root = new MyProject("p", "我的项目");
        MyClass userClass = new MyClass(root, "c4", "用户", "", stereotypes);

        PumlDiagram diagram = newDiagram();
        new PumlClassExporter().to(diagram, wrap(userClass));

        assertTrue(diagram.toString().contains("class \"用户\" as 用户 {"));
    }


    private MyModel findModel(MyProject root) {
        for (MyElement_old2 e : root.getSubElements()) {
            if (e instanceof MyModel) {
                return (MyModel) e;
            }
        }
        throw new AssertionError("no model");
    }

    private MyPackage_old findPackage(MyProject root, String name) {
        MyModel model = findModel(root);
        for (MyElement_old2 e : model.getSubElements()) {
            if (e instanceof MyPackage_old && name.equals(e.getName())) {
                return (MyPackage_old) e;
            }
        }
        throw new AssertionError("no package: " + name);
    }

    private MyClass findClass(MyProject root, String name) {
        MyPackage_old pkg = findPackage(root, "订单");
        for (MyElement_old2 e : pkg.getSubElements()) {
            if (e instanceof MyClass && name.equals(e.getName())) {
                return (MyClass) e;
            }
        }
        throw new AssertionError("no class: " + name);
    }
}
