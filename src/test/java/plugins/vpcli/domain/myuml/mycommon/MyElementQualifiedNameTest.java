package plugins.vpcli.domain.myuml.mycommon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.mymodel.MyModel;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.myproject.MyProject;
import plugins.vpcli.domain.myuml.myusecase.MyUseCase;

class MyElementQualifiedNameTest {

    // 构建与 doc/订单概览-目标态.puml 对应的模型树
    private static class Tree {
        MyProject root;
        MyModel model;
        MyPackage orderPkg;
        MyClass orderClass;
        MyClass orderItemClass;
        MyPackage productPkg;
        MyClass productClass;
        MyClass userClass;
        MyUseCase addUserUseCase;
        MyClass specialNameClass;
    }

    private Tree buildModelTree(boolean withSpecialName) {
        Tree t = new Tree();
        t.root = new MyProject("p", "我的项目");
        t.model = new MyModel(t.root, "m", "领域模型", "");
        t.orderPkg = new MyPackage(t.model, "pk1", "订单", "");
        t.orderClass = new MyClass(t.orderPkg, "c1", "订单", "");
        t.orderItemClass = new MyClass(t.orderPkg, "c2", "订单项", "");
        t.productPkg = new MyPackage(t.orderPkg, "pk2", "产品", "");
        t.productClass = new MyClass(t.productPkg, "c3", "产品", "");
        t.userClass = new MyClass(t.root, "c4", "用户", "");
        t.addUserUseCase = new MyUseCase(t.root, "u1", "增加用户", "");
        if (withSpecialName) {
            t.specialNameClass = new MyClass(t.model, "c5", "A-B C", "");
        }
        return t;
    }

    @Test
    void shouldBuildQualifiedNameForNestedElements() {
        Tree t = buildModelTree(false);
        assertEquals("领域模型", t.model.getQualifiedName());
        assertEquals("领域模型.订单", t.orderPkg.getQualifiedName());
        assertEquals("领域模型.订单.订单", t.orderClass.getQualifiedName());
        assertEquals("领域模型.订单.订单项", t.orderItemClass.getQualifiedName());
        assertEquals("领域模型.订单.产品", t.productPkg.getQualifiedName());
        assertEquals("领域模型.订单.产品.产品", t.productClass.getQualifiedName());
    }

    @Test
    void shouldNotPrefixTopLevelElementsWithProjectOrModel() {
        Tree t = buildModelTree(false);
        // 顶层类/用例：只含自身名字，不包含 "我的项目"/"领域模型" 前缀
        assertEquals("用户", t.userClass.getQualifiedName());
        assertEquals("增加用户", t.addUserUseCase.getQualifiedName());
    }

    @Test
    void shouldKeepNonLetterDigitsInQualifiedName() {
        Tree t = buildModelTree(true);
        assertEquals("领域模型.A-B C", t.specialNameClass.getQualifiedName());
    }
}
