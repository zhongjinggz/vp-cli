package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IQualifier;

import com.vp.plugin.model.IRelationshipEnd;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;
import plugins.vpcli.util.Strings;

import static plugins.vpcli.util.Strings.*;

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
    // labels := : [<] [name] [<<stereotype>>]* [>]

    @NonNull String formatAssociation(IAssociation association) {

        return buildStringPatcher(fromAlias(association))
            .patch(fromMultiplicityAndRole(association))
            .patch(associationLine(association))
            .patch(ToMultiplicityAndRole(association))
            .patch(toAlias(association))
            .patch(labels(association))
            .toString();
    }

    private @NonNull String ToMultiplicityAndRole(IAssociation association) {
        return getMultiplicityAndRole(association.getToEnd());
    }

    private @NonNull String fromMultiplicityAndRole(IAssociation association) {
        return getMultiplicityAndRole(association.getFromEnd());
    }

    private String getMultiplicityAndRole(IRelationshipEnd fromRelationshipEnd) {
        IAssociationEnd fromAssociationEnd = (IAssociationEnd) fromRelationshipEnd;

        StringBuilder roleAndMultiplicity = new StringBuilder();

        String role = fromAssociationEnd.getName();
        if (isNotBlank(role)) {
            roleAndMultiplicity.append("\"").append(role);
        }

        String multiplicity = normalizeMultiplicity(fromAssociationEnd.getMultiplicity());
        if (isNotBlank(multiplicity)) {
            if (roleAndMultiplicity.length() > 0) {
                roleAndMultiplicity.append(' ');
            } else {
                roleAndMultiplicity.append("\"");
            }
            roleAndMultiplicity.append(multiplicity);
        }

        if (roleAndMultiplicity.length() > 0) {
            roleAndMultiplicity.append("\"");
        }

        String qualifiers = qualifiers(fromAssociationEnd);
        String base = roleAndMultiplicity.toString();
        if (isNotBlank(qualifiers)) {
            if (isBlank(base)) {
                return qualifiers;
            }
            return qualifiers + " " + base;
        }
        return base;
    }

    private String qualifiers(IAssociationEnd associationEnd) {
        List<String> names = new ArrayList<>();
        IQualifier qualifier = associationEnd.getQualifier();
        if (qualifier != null) {
            for (IAttribute attribute : qualifier.toAttributeArray()) {
                if (attribute != null && isNotBlank(attribute.getName())) {
                    names.add(attribute.getName());
                }
            }
        }
        if (names.isEmpty()) {
            return "";
        }
        return "[" + String.join(", ", names) + "]";
    }

    private String fromAlias(IAssociation association) {
        return getPumlAlias(association.getFromEnd().getModelElement());
    }

    private String toAlias(IAssociation association) {
        return getPumlAlias(association.getToEnd().getModelElement());
    }

    private @NonNull String labels(IAssociation association) {

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
        String result;
        if (isBlank(name)) {
            result = stereotypes;
        } else if (isBlank(stereotypes)) {
            result = name;
        } else {
            result = name + " " + stereotypes;
        }
        return result;
    }

    private @NonNull String associationLine(IAssociation association) {
        return fromPoint((IAssociationEnd) association.getFromEnd()) +
            "--" +
            toPoint((IAssociationEnd) association.getToEnd());
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

}
