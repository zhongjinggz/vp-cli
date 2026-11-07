package plugins.vpcli.domain.outputmodel.pumldiagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporterRepository;

/**
 * 针对 {@link PumlDiagramFactory#build}：仅渲染顶层形状、跳过嵌套形状、遍历连接线，
 * 以及 shapes/connectors 为 null 时的安全处理。
 */
class PumlDiagramFactoryTest {

    private final PumlElementExporterRepository stubRepo = mock(PumlElementExporterRepository.class);

    @Test
    void onlyTopLevelShapesAreExported() {
        IDiagramUIModel diagramUi = mock(IDiagramUIModel.class);
        when(diagramUi.getName()).thenReturn("root");

        IShapeUIModel top = mock(IShapeUIModel.class);
        when(top.getParent()).thenReturn(null);
        IShapeUIModel nested = mock(IShapeUIModel.class);
        when(nested.getParent()).thenReturn(top);

        when(diagramUi.toShapeUIModelArray()).thenReturn(
            new IShapeUIModel[]{top, nested});
        when(diagramUi.toConnectorUIModelArray()).thenReturn(
            new IConnectorUIModel[0]);

        PumlElementExporter topExporter = mock(PumlElementExporter.class);
        when(stubRepo.findBy(top)).thenReturn(topExporter);

        PumlDiagram diagram = new PumlDiagramFactory(stubRepo).build(diagramUi);

        verify(stubRepo).findBy(top);
        // 嵌套形状不应被分发（由 PumlBlockExporter 递归渲染）
        org.mockito.Mockito.verify(stubRepo,
            org.mockito.Mockito.never()).findBy(nested);
        assertEquals("root", diagram.getName());
    }

    @Test
    void connectorsAreDispatched() {
        PumlElementExporter exporter = mock(PumlElementExporter.class);
        IDiagramUIModel diagramUi = mock(IDiagramUIModel.class);
        when(diagramUi.getName()).thenReturn("root");
        when(diagramUi.toShapeUIModelArray()).thenReturn(null);
        IConnectorUIModel connector = mock(IConnectorUIModel.class);
        when(diagramUi.toConnectorUIModelArray()).thenReturn(
            new IConnectorUIModel[]{connector});
        when(stubRepo.findBy(connector)).thenReturn(exporter);

        new PumlDiagramFactory(stubRepo).build(diagramUi);

        verify(stubRepo).findBy(connector);
    }

    @Test
    void nullShapesAndConnectorsAreHandled() {
        IDiagramUIModel diagramUi = mock(IDiagramUIModel.class);
        when(diagramUi.getName()).thenReturn("root");
        when(diagramUi.toShapeUIModelArray()).thenReturn(null);
        when(diagramUi.toConnectorUIModelArray()).thenReturn(null);

        PumlDiagram diagram = new PumlDiagramFactory(stubRepo).build(diagramUi);

        assertTrue(diagram.toString().contains("@startuml"));
        org.mockito.Mockito.verifyNoInteractions(stubRepo);
    }
}
