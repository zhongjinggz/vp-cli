package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IStereotype;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

/**
 * 步 #21：9 类特殊依赖导出器渲染与普通依赖一致（`别名1 ..> 别名2`）。
 * 连接线模型统一按 {@link IRelationship} 读取 getFrom()/getTo()/getName()。
 * 连线渲染细节（`:` 标签、泛型）已在 PumlRelationshipStereotypeTest 覆盖，此处聚焦端点与 shape type。
 */
class PumlSpecialDependencyExporterTest {

    @Test
    void usageRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlUsageExporter().export(diagram, edgeOf("class1", "class2"));
        assertTrue(diagram.toString().contains("class1 ..> class2"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void bindingDependencyRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlBindingDependencyExporter().export(diagram, edgeOf("Bind", "Target"));
        assertTrue(diagram.toString().contains("Bind ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void permissionRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlPermissionExporter().export(diagram, edgeOf("Permit", "Target"));
        assertTrue(diagram.toString().contains("Permit ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void accessRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlAccessExporter().export(diagram, edgeOf("Access", "Target"));
        assertTrue(diagram.toString().contains("Access ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void importRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlImportExporter().export(diagram, edgeOf("Import", "Target"));
        assertTrue(diagram.toString().contains("Import ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void mergeRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlMergeExporter().export(diagram, edgeOf("Merge", "Target"));
        assertTrue(diagram.toString().contains("Merge ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void instantiationRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlInstantiationExporter().export(diagram, edgeOf("Instantiate", "Target"));
        assertTrue(diagram.toString().contains("Instantiate ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void substitutionRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlSubstitutionExporter().export(diagram, edgeOf("Substitute", "Target"));
        assertTrue(diagram.toString().contains("Substitute ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void abstractionRendersDashedArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlAbstractionExporter().export(diagram, edgeOf("Abstraction", "Target"));
        assertTrue(diagram.toString().contains("Abstraction ..> Target"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void dependencyReusesStereotypeLabelWhenPresent() {
        PumlDiagram diagram = new PumlDiagram("d");
        IRelationship rel = mock(IRelationship.class);
        when(rel.getName()).thenReturn("name");
        IStereotype s = mock(IStereotype.class);
        when(s.getName()).thenReturn("use");
        when(rel.stereotypeModelIterator()).thenReturn(List.of(s).iterator());
        new PumlUsageExporter().export(diagram, edgeOf(rel, "class1", "class2"));
        assertTrue(diagram.toString().contains("class1 ..> class2: name <<use>>"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void eachSpecialExporterHasDistinctShapeType() {
        String usage = new PumlUsageExporter().getShapeType();
        String bind = new PumlBindingDependencyExporter().getShapeType();
        String permission = new PumlPermissionExporter().getShapeType();
        String access = new PumlAccessExporter().getShapeType();
        String imp = new PumlImportExporter().getShapeType();
        String merge = new PumlMergeExporter().getShapeType();
        String inst = new PumlInstantiationExporter().getShapeType();
        String subst = new PumlSubstitutionExporter().getShapeType();
        String abstraction = new PumlAbstractionExporter().getShapeType();

        assertTrue(usage.endsWith("Usage"));
        assertTrue(bind.endsWith("BindingDependency"));
        assertTrue(permission.endsWith("Permission"));
        assertTrue(access.endsWith("Access"));
        assertTrue(imp.endsWith("Import"));
        assertTrue(merge.endsWith("Merge"));
        assertTrue(inst.endsWith("Instantiation"));
        assertTrue(subst.endsWith("Substitution"));
        assertTrue(abstraction.endsWith("Abstraction"));
        // 各 shape type 互不相同，工厂才能分别分发。
        assertNotEquals(usage, bind);
        assertNotEquals(bind, permission);
        assertNotEquals(permission, access);
        assertNotEquals(access, imp);
        assertNotEquals(imp, merge);
        assertNotEquals(merge, inst);
        assertNotEquals(inst, subst);
        assertNotEquals(subst, abstraction);
    }

    // ---- helpers ----

    private static IModelElement model(String name) {
        IModelElement m = mock(IModelElement.class);
        when(m.getName()).thenReturn(name);
        return m;
    }

    private static IDiagramElement edgeOf(String from, String to) {
        IRelationship rel = mock(IRelationship.class);
        when(rel.getName()).thenReturn("");
        when(rel.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());
        return edgeOf(rel, from, to);
    }

    private static IDiagramElement edgeOf(IRelationship rel, String from, String to) {
        // 先建好两端 mock，再桩关系：避免在 thenReturn 实参内再 when()（会触发 UnfinishedStubbing）。
        IModelElement fromModel = model(from);
        IModelElement toModel = model(to);
        when(rel.getFrom()).thenReturn(fromModel);
        when(rel.getTo()).thenReturn(toModel);
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(rel);
        return e;
    }
}
