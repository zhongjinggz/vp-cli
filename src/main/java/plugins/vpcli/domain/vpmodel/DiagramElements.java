package plugins.vpcli.domain.vpmodel;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.shape.IClassUIModel;

import java.util.Objects;

public class DiagramElements {
    private DiagramElements() {}

    public static boolean isInterfaceBall(IDiagramElement vpDiagramElement) {
        return Objects.equals(vpDiagramElement.getShapeType(), IShapeTypeConstants.SHAPE_TYPE_CLASS)
            && ((IClassUIModel) vpDiagramElement).isInterfaceBall();
    }

}
