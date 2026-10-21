package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.myuml.mycommon.MyElement;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

class PumlAliasTest {

    // 暴露基类 protected 的 getPumlAlias 供测试直接验证
    private static class Exposer extends PumlElementExporter {
        String alias(MyElement myElement) {
            return getPumlAlias(myElement);
        }

        @Override
        public void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
            // 不通过 to() 输出，仅用于暴露别名方法
        }
    }

    private Exposer exposer = new Exposer();

    @Test
    void shouldBuildAliasFromQualifiedName() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "");
        MyPackage orderPkg = new MyPackage(model, "pk1", "订单", "");
        MyClass orderClass = new MyClass(orderPkg, "c1", "订单", "");
        MyClass orderItemClass = new MyClass(orderPkg, "c2", "订单项", "");
        MyPackage productPkg = new MyPackage(orderPkg, "pk2", "产品", "");
        MyClass productClass = new MyClass(productPkg, "c3", "产品", "");

        assertEquals("领域模型", exposer.alias(model));
        assertEquals("领域模型__订单", exposer.alias(orderPkg));
        assertEquals("领域模型__订单__订单", exposer.alias(orderClass));
        assertEquals("领域模型__订单__订单项", exposer.alias(orderItemClass));
        assertEquals("领域模型__订单__产品", exposer.alias(productPkg));
        assertEquals("领域模型__订单__产品__产品", exposer.alias(productClass));
    }

    @Test
    void shouldNotPrefixTopLevelElementsWithProjectOrModel() {
        MyProject root = new MyProject("p", "我的项目");
        MyClass userClass = new MyClass(root, "c4", "用户", "");
        MyUseCase useCase = new MyUseCase(root, "u1", "增加用户", "");

        assertEquals("用户", exposer.alias(userClass));
        assertEquals("增加用户", exposer.alias(useCase));
    }

    @Test
    void shouldSanitizeNonLetterDigitCharactersPerSegment() {
        MyProject root = new MyProject("p", "我的项目");
        MyModel model = new MyModel(root, "m", "领域模型", "");
        MyClass special = new MyClass(model, "c5", "A-B C", "");

        // 段内非字母数字转 "_"，"." 保持全限定名，别名中以 "__" 分隔
        assertEquals("领域模型.A-B C", special.getQualifiedName());
        assertEquals("领域模型__A_B_C", exposer.alias(special));
    }
}
