package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;

import static plugins.vpcli.util.StringUtil.isBlank;
import static plugins.vpcli.util.StringUtil.isNotBlank;

@Singleton
public class PumlAssociationExporter extends PumlElementExporter {
    @Inject
    public PumlAssociationExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION;
    }

    @Override
    public void export(PumlDiagram result, IDiagramElement vpDiagramElement) {
        IAssociation association = (IAssociation) vpDiagramElement.getModelElement();

        String text = formatAssociation(association);

        result.writeTerminalShape(text);
    }

    // association := fromElementAlias [fromMultiplicity&role] line [toMultiplicity&role] toElementAlias [labels]
    // labels := :[name] [<<stereotype>>]* [>]

    @NonNull String formatAssociation(IAssociation association) {
        IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
        IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();


        String fromMultiplicityAndRole = buildMultiplicityAndRole(fromEnd.getName(), normalizeMultiplicity(fromEnd.getMultiplicity()));

        String toMultiplicityAndRole = buildMultiplicityAndRole(toEnd.getName(), normalizeMultiplicity(toEnd.getMultiplicity()));

        String result = getPumlAlias(fromEnd.getModelElement());
        if (!fromMultiplicityAndRole.isEmpty()) {
            result += " \"" + fromMultiplicityAndRole + "\"";
        }
        result = result + " " + buildAssociationLine(fromEnd, toEnd) + " ";
        if (!toMultiplicityAndRole.isEmpty()) {
            result += "\"" + toMultiplicityAndRole + "\" ";
        }
        result += getPumlAlias(toEnd.getModelElement());


        String associationLabels = getAssociationLabels(association);
        result += associationLabels;

        return result;
    }

    private @NonNull String getAssociationLabels(IAssociation association) {

        String name = association.getName();
        String stereotypes = renderStereotypes(association);

        String result = joinNameAndStereotypes(name, stereotypes);

        if (isNotBlank(name)) {
            result = patchDirection(association, result);
        }

        if (isNotBlank(result)) {
            result = ": " + result;
        }
        return result;
    }

    private @NotNull String patchDirection(
        @NotNull IAssociation association,
        @NotNull String nameAndStereotype
    ) {
        String result;

        switch (association.getDirection()) {
            case IAssociation.DIRECTION_FROM_TO:
                result = nameAndStereotype + " >";
                break;
            case IAssociation.DIRECTION_TO_FROM:
                result = "< " + nameAndStereotype;
                break;
            default:
                result = nameAndStereotype;
        }

        return result;
    }

    private static @NotNull String joinNameAndStereotypes(String name, String stereotypes) {
        String result = "";
        if (isBlank(name)) {
            result = stereotypes;
        } else if (isBlank(stereotypes)) {
            result = name;
        } else {
            result = name + " " + stereotypes;
        }
        return result;
    }

    private @NonNull String buildAssociationLine(IAssociationEnd fromEnd, IAssociationEnd toEnd) {
        return fromPoint(fromEnd) + "--" + toPoint(toEnd);
    }

    private String toPoint(IAssociationEnd toEnd) {
        return shapeFor(toEnd, false);
    }

    private String fromPoint(IAssociationEnd fromEnd) {
        return shapeFor(fromEnd, true);
    }

    private String shapeFor(IAssociationEnd toEnd, boolean isFromEnd) {
        ShapeKind result;
        String aggregation = toEnd.getAggregationKind();
        if (IAssociationEnd.AGGREGATION_KIND_shared.equals(aggregation)) {
            result = ShapeKind.DIAMOND;
        } else if (IAssociationEnd.AGGREGATION_KIND_composite.equals(aggregation)) {
            result = ShapeKind.SOLID_DIAMOND;
        } else {
            switch (toEnd.getNavigable()) {
                case IAssociationEnd.NAVIGABLE_NAVIGABLE:
                    result = isFromEnd ? ShapeKind.REVERSE_ARROW : ShapeKind.ARROW;
                    break;
                case IAssociationEnd.NAVIGABLE_NON_NAVIGABLE:
                    result = ShapeKind.CROSS;
                    break;
                default:
                    result = ShapeKind.NONE;
                    break;
            }
        }
        return result.getText();
    }

    /**
     * 与 MyAssociationEnd.setMultiplicity 一致：null 或 "Unspecified" 归空串。
     */
    private String normalizeMultiplicity(String multiplicity) {
        if (multiplicity == null || IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(multiplicity)) {
            return "";
        }
        return multiplicity;
    }

    /**
     * 合并 role 与 multiplicity 为单个引号标签，空的部分省略。
     *
     * @param role
     * @param multiplicity
     */
    public String buildMultiplicityAndRole(String role, String multiplicity) {
        StringBuilder sb = new StringBuilder();
        if (role != null && !role.isEmpty()) {
            sb.append(role);
        }
        if (multiplicity != null && !multiplicity.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(multiplicity);
        }

        return sb.toString();
    }

}
