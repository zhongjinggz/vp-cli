package plugins.vpcli.domain.outputmodel.pumlexporter.pumldependency;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;

/**
 * 覆盖 {@link PumlRealizationExporter} 与 {@link PumlUsageExporter} 的
 * interface-ball 分支：命中时用 ` -- `/` --( `，未命中回退默认箭头。
 */
class PumlRealizationUsageInterfaceBallTest {

    // ---- Realization ----

    @Test
    void realizationWithoutInterfaceBallUsesRealizationArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlRealizationExporter().export(diagram,
            edge("A", "B", false, false));
        assertTrue(diagram.toString().contains("A <|.. B"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void realizationWithFromInterfaceBallUsesPlainDoubleDash() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlRealizationExporter().export(diagram,
            edge("A", "B", true, false));
        assertTrue(diagram.toString().contains("A -- B"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void realizationIgnoresToShapeInterfaceBall() {
        // Realization 只看 fromShape
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlRealizationExporter().export(diagram,
            edge("A", "B", false, true));
        assertTrue(diagram.toString().contains("A <|.. B"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void realizationWithNonConnectorElementFallsBackToRealizationArrow() {
        // 非 IConnectorUIModel：isRealizingInterfaceBall 返回 false，走默认箭头
        IDiagramElement nonConnector = mock(IDiagramElement.class);
        assertTrue(new PumlRealizationExporter().getArrow(nonConnector).equals(" <|.. "));
    }

    // ---- Usage ----

    @Test
    void usageWithoutInterfaceBallUsesDependencyArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlUsageExporter().export(diagram,
            edge("A", "B", false, false));
        assertTrue(diagram.toString().contains("A ..> B"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void usageWithToInterfaceBallUsesUsageArrow() {
        PumlDiagram diagram = new PumlDiagram("d");
        new PumlUsageExporter().export(diagram,
            edge("A", "B", false, true));
        assertTrue(diagram.toString().contains("A --( B"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void usageWithNonConnectorElementFallsBackToDependencyArrow() {
        // 非 IConnectorUIModel：isUseInterfaceBall 返回 false，走 super（依赖）箭头
        IDiagramElement nonConnector = mock(IDiagramElement.class);
        assertTrue(new PumlUsageExporter().getArrow(nonConnector).equals(" ..> "));
    }

    // ---- helpers ----

    private static IDiagramElement edge(String fromName, String toName,
                                        boolean fromBall, boolean toBall) {
        IModelElement fromModel = mock(IModelElement.class);
        when(fromModel.getName()).thenReturn(fromName);
        IModelElement toModel = mock(IModelElement.class);
        when(toModel.getName()).thenReturn(toName);

        IClassUIModel fromShape = mock(IClassUIModel.class);
        when(fromShape.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_CLASS);
        when(fromShape.isInterfaceBall()).thenReturn(fromBall);
        when(fromShape.getModelElement()).thenReturn(fromModel);

        IClassUIModel toShape = mock(IClassUIModel.class);
        when(toShape.getShapeType()).thenReturn(IShapeTypeConstants.SHAPE_TYPE_CLASS);
        when(toShape.isInterfaceBall()).thenReturn(toBall);
        when(toShape.getModelElement()).thenReturn(toModel);

        IRelationship rel = mock(IRelationship.class);
        when(rel.getName()).thenReturn("");
        when(rel.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());
        when(rel.getFrom()).thenReturn(fromModel);
        when(rel.getTo()).thenReturn(toModel);

        IConnectorUIModel e = mock(IConnectorUIModel.class);
        when(e.getModelElement()).thenReturn(rel);
        when(e.getFromShape()).thenReturn(fromShape);
        when(e.getToShape()).thenReturn(toShape);
        return e;
    }
}
