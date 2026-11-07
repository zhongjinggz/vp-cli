package vpcli.plugin.domain.outputmodel.pumlexporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.shape.IClassUIModel;
import org.junit.jupiter.api.Test;
import vpcli.plugin.domain.outputmodel.pumlexporter.*;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.PumlAssociationClassExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.PumlAssociationExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.PumlClassExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.PumlGeneralizationExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.PumlInterfaceBallExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass.PumlNAryExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlAbstractionExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlAccessExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlBindingDependencyExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlDependencyExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlDeriveExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlInstantiationExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlPermissionExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlRealizationExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlRefineExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlSubstitutionExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlTraceExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumldependency.PumlUsageExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlImportExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlMergeExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlModelExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnamespace.PumlPackageExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnote.PumlAnchorExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlnote.PumlNoteExporter;
import vpcli.plugin.domain.outputmodel.pumlexporter.pumlusecase.PumlUseCaseExporter;

/**
 * 针对 {@link PumlElementExporterRepository}：构造器注册各 exporter、
 * findBy 的 interface-ball / 精确类型 / 未知类型回退三条分支。
 */
class PumlElementExporterRepositoryTest {

    private final PumlElementExporterRepository repo = buildRepo();

    @Test
    void findByReturnsUnimplementedForUnknownShapeType() {
        IDiagramElement element = mock(IDiagramElement.class);
        when(element.getShapeType()).thenReturn("some-unknown-type");

        assertInstanceOf(PumlUnimplementedExporter.class, repo.findBy(element));
    }

    @Test
    void findByReturnsInterfaceBallExporterWhenInterfaceBall() {
        IClassUIModel element = mock(IClassUIModel.class);
        when(element.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_CLASS);
        when(element.isInterfaceBall()).thenReturn(true);

        assertInstanceOf(PumlInterfaceBallExporter.class, repo.findBy(element));
    }

    @Test
    void findByReturnsClassExporterWhenNotInterfaceBallButClassShape() {
        IClassUIModel element = mock(IClassUIModel.class);
        when(element.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_CLASS);
        when(element.isInterfaceBall()).thenReturn(false);

        assertInstanceOf(PumlClassExporter.class, repo.findBy(element));
    }

    @Test
    void findByMapsEveryKnownShapeTypeToItsExporter() {
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_CLASS, PumlClassExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION, PumlAssociationExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION_CLASS, PumlAssociationClassExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_NARY, PumlNAryExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION, PumlGeneralizationExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_CONTAINMENT, PumlContainmentExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_PACKAGE, PumlPackageExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_MODEL, PumlModelExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_USE_CASE, PumlUseCaseExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_DEPENDENCY, PumlDependencyExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_REALIZATION, PumlRealizationExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_USAGE, PumlUsageExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_BINDING_DEPENDENCY, PumlBindingDependencyExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_PERMISSION, PumlPermissionExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_ACCESS, PumlAccessExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_IMPORT, PumlImportExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_MERGE, PumlMergeExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_INSTANTIATION, PumlInstantiationExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_SUBSTITUTION, PumlSubstitutionExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_ABSTRACTION, PumlAbstractionExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_DERIVE, PumlDeriveExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_REFINE, PumlRefineExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_TRACE, PumlTraceExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_NOTE, PumlNoteExporter.class);
        assertMapped(IShapeTypeConstants.SHAPE_TYPE_ANCHOR, PumlAnchorExporter.class);
        // interface ball uses pseudo type
        assertMapped(PumlInterfaceBallExporter.SHAPE_TYPE, PumlInterfaceBallExporter.class);
    }

    private void assertMapped(String shapeType, Class<? extends PumlElementExporter> expected) {
        IDiagramElement element = mock(IDiagramElement.class);
        when(element.getShapeType()).thenReturn(shapeType);
        if (IShapeTypeConstants.SHAPE_TYPE_CLASS.equals(shapeType)) {
            // findBy 会先对 class shape 做 isInterfaceBall 强转
            IClassUIModel klass = mock(IClassUIModel.class);
            when(klass.getShapeType()).thenReturn(shapeType);
            when(klass.isInterfaceBall()).thenReturn(false);
            element = klass;
        }
        assertEquals(expected, repo.findBy(element).getClass(),
            "shapeType=" + shapeType + " 应映射到 " + expected.getSimpleName());
    }

    private static PumlElementExporterRepository buildRepo() {
        return new PumlElementExporterRepository(
            new PumlUnimplementedExporter(),
            new PumlModelRelationshipContainerExporter(),
            new PumlNoteExporter(),
            new PumlAnchorExporter(),
            new PumlClassExporter(),
            new PumlAssociationExporter(),
            new PumlAssociationClassExporter(),
            new PumlNAryExporter(),
            new PumlGeneralizationExporter(),
            new PumlContainmentExporter(),
            new PumlInterfaceBallExporter(),
            new PumlPackageExporter(),
            new PumlModelExporter(),
            new PumlUseCaseExporter(),
            new PumlDependencyExporter(),
            new PumlRealizationExporter(),
            new PumlUsageExporter(),
            new PumlBindingDependencyExporter(),
            new PumlPermissionExporter(),
            new PumlAccessExporter(),
            new PumlImportExporter(),
            new PumlMergeExporter(),
            new PumlInstantiationExporter(),
            new PumlSubstitutionExporter(),
            new PumlAbstractionExporter(),
            new PumlDeriveExporter(),
            new PumlRefineExporter(),
            new PumlTraceExporter()
        );
    }
}
