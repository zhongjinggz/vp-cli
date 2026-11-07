package plugins.vpcli.domain.outputmodel.pumlexporter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlClassExporter;

/**
 * 覆盖 {@link PumlBlockExporter#exportBlockBody}：仅对 {@link IShapeUIModel} 遍历 child，
 * 且仅导出 getModelElement() != null 的孩子；同时覆盖
 * {@link PumlUnimplementedExporter#export} 的空/非空 vpElement 两条三元分支。
 */
class PumlBlockBodyAndUnimplementedTest {

    @Test
    void blockBodyExportsOnlyShapesAndNonNullModelChildren() {
        PumlDiagram diagram = new PumlDiagram("d");

        IClass childModel = clazzModel("Child");
        IShapeUIModel childWithModel = classShapeOf(childModel);
        // getModelElement() 为 null 的孩子应被跳过
        IShapeUIModel childWithoutModel = classShapeOf(null);

        IShapeUIModel rootShape = mock(IShapeUIModel.class);
        IModelElement rootModel = model("package", "pkg1");
        when(rootShape.getModelElement()).thenReturn(rootModel);
        when(rootShape.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_PACKAGE);
        when(rootShape.toChildArray())
            .thenReturn(new IShapeUIModel[]{childWithModel, childWithoutModel});

        // 用仓库驱动分发到 PumlPackageExporter（继承 PumlBlockExporter）
        PumlElementExporterRepository repo = repositoryWithRegistry();
        repo.findBy(rootShape).export(diagram, rootShape);

        String out = diagram.toString();
        assertTrue(out.contains("Child"),
            () -> "存在模型元素的孩子应被导出。实际输出：\n" + out);
    }

    @Test
    void blockBodyWithNonShapeModelElementIsNoopForUnimplementedChild() {
        // 孩子 getModelElement() 返回 null → 跳过
        PumlDiagram diagram = new PumlDiagram("d");

        IShapeUIModel rootShape = mock(IShapeUIModel.class);
        IModelElement rootModel = model("package", "pkg1");
        when(rootShape.getModelElement()).thenReturn(rootModel);
        when(rootShape.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_PACKAGE);
        when(rootShape.toChildArray())
            .thenReturn(new IShapeUIModel[]{});

        // 触发 block 导出但孩子需含一个 model == null 的孩子 → 跳过
        IShapeUIModel childWithoutModel = classShapeOf(null);
        when(rootShape.toChildArray())
            .thenReturn(new IShapeUIModel[]{childWithoutModel});

        PumlElementExporterRepository repo = repositoryWithRegistry();
        assertDoesNotThrow(() -> repo.findBy(rootShape).export(diagram, rootShape));
    }

    // ---- UnimplementedExporter ----

    @Test
    void unimplementedWithNullVpElementDoesNotThrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        IDiagramElement element = mock(IDiagramElement.class);
        when(element.getModelElement()).thenReturn(null);
        when(element.getShapeType()).thenReturn("weird");

        assertDoesNotThrow(() -> new PumlUnimplementedExporter().export(diagram, element));
    }

    @Test
    void unimplementedWithNonNullVpElementDoesNotThrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        IModelElement m = model("weirdmodel", "W");
        IDiagramElement element = mock(IDiagramElement.class);
        when(element.getModelElement()).thenReturn(m);
        when(element.getShapeType()).thenReturn("weird");

        assertDoesNotThrow(() -> new PumlUnimplementedExporter().export(diagram, element));
    }

    @Test
    void baseExportMethodIsNoop() {
        // 覆盖 PumlElementExporter 的空 export()（L26-27），经不覆写的子类触发。
        // 因 diagram 固定带 @startuml 头，这里只断言未产生任何块/语句正文。
        PumlDiagram diagram = new PumlDiagram("d");
        IDiagramElement element = mock(IDiagramElement.class);
        assertDoesNotThrow(() ->
            new PumlModelRelationshipContainerExporter().export(diagram, element));
        String out = diagram.toString();
        assertTrue(out.contains("@startuml"));
        assertTrue(!out.contains("as ") && !out.contains(" {"), "空导出不应写入元素。实际输出：\n" + out);
    }

    // ---- helpers ----

    private static IClass clazzModel(String name) {
        IClass c = mock(IClass.class);
        when(c.getName()).thenReturn(name);
        when(c.getModelType()).thenReturn("Class");
        when(c.getId()).thenReturn("id-" + name);
        when(c.attributeIterator()).thenReturn(Collections.emptyIterator());
        when(c.operationIterator()).thenReturn(Collections.emptyIterator());
        when(c.enumerationLiteralIterator()).thenReturn(Collections.emptyIterator());
        when(c.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());
        when(c.toStereotypeModelArray()).thenReturn(null);
        return c;
    }

    private static IModelElement model(String type, String name) {
        IModelElement m = mock(IModelElement.class);
        when(m.getModelType()).thenReturn(type);
        when(m.getName()).thenReturn(name);
        when(m.getId()).thenReturn("id-" + name);
        when(m.toStereotypeModelArray()).thenReturn(null);
        return m;
    }

    private static IShapeUIModel classShapeOf(IModelElement model) {
        com.vp.plugin.diagram.shape.IClassUIModel s =
            mock(com.vp.plugin.diagram.shape.IClassUIModel.class);
        when(s.getModelElement()).thenReturn(model);
        when(s.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_CLASS);
        when(s.isInterfaceBall()).thenReturn(false);
        when(s.toChildArray()).thenReturn(new IShapeUIModel[0]);
        return s;
    }

    private static PumlElementExporterRepository repositoryWithRegistry() {
        return new PumlElementExporterRepository(
            new PumlUnimplementedExporter(),
            new PumlModelRelationshipContainerExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlnote.PumlNoteExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlnote.PumlAnchorExporter(),
            new PumlClassExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlAssociationExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlAssociationClassExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlNAryExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlGeneralizationExporter(),
            new PumlContainmentExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass.PumlInterfaceBallExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlPackageExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlModelExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlusecase.PumlUseCaseExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlDependencyExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlRealizationExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlUsageExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlBindingDependencyExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlPermissionExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlAccessExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlImportExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumlnamespace.PumlMergeExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlInstantiationExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlSubstitutionExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlAbstractionExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlDeriveExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlRefineExporter(),
            new plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency.PumlTraceExporter()
        );
    }
}
