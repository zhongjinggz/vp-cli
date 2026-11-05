package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IQualifier;

import com.vp.plugin.model.IRelationshipEnd;

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
        // association := fromElementAlias [fromMultiplicity&role] line [toMultiplicity&role] toElementAlias [labels]
        // labels := : [<] [name] [<<stereotype>>]* [>]

        IAssociation association = (IAssociation) vpDiagramElement.getModelElement();

        result.writeText(
            Strings.buildStringPatcher(fromAlias(association))
                .patch(fromQualifier(association))
                .patch(fromMultiplicityAndRole(association))
                .patch(associationLine(association))
                .patch(ToMultiplicityAndRole(association))
                .patch(toQualifier(association))
                .patch(toAlias(association))
                .patch(labels(association))
                .toString()
        );
    }

    private @NonNull String ToMultiplicityAndRole(IAssociation association) {
        return getMultiplicityAndRole(association.getToEnd());
    }

    private @NonNull String fromMultiplicityAndRole(IAssociation association) {
        return getMultiplicityAndRole(association.getFromEnd());
    }

    private String getMultiplicityAndRole(IRelationshipEnd relationshipEnd) {
        IAssociationEnd fromAssociationEnd = (IAssociationEnd) relationshipEnd;

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

        return roleAndMultiplicity.toString();
    }

    private @NotNull String fromQualifier(IAssociation association) {
        return getQualifier((IAssociationEnd) association.getFromEnd());
    }

    private @NotNull String toQualifier(IAssociation association) {
        return getQualifier((IAssociationEnd) association.getToEnd());
    }

    private String getQualifier(IAssociationEnd associationEnd) {
        StringBuilder result = new StringBuilder();

        IQualifier qualifier = associationEnd.getQualifier();
        if (qualifier != null) {
            for (IAttribute attribute : qualifier.toAttributeArray()) {
                if (isNotBlank(attribute.getName())) {
                    if (result.length() == 0) {
                        result.append("[");
                    } else {
                        result.append("\\n");
                    }
                    result.append(attribute.getName());
                }
            }
            if (result.length() > 0) {
                result.append("]");
            }
        }

        return result.toString();
    }

    private String fromAlias(IAssociation association) {
        return getPumlAlias(association.getFromEnd().getModelElement());
    }

    private String toAlias(IAssociation association) {
        return getPumlAlias(association.getToEnd().getModelElement());
    }

    private @NonNull String labels(IAssociation association) {

        var nameAndStereotype = buildStringPatcher(association.getName())
            .patch(renderStereotypes(association))
            .toString();

        var result = patchDirection(association, nameAndStereotype);

        if (isNotBlank(result)) {
            result = ": " + result;
        }
        return result;
    }

    private @NotNull String patchDirection(
        @NotNull IAssociation association,
        @NotNull String nameAndStereotype
    ) {
        if (isBlank(association.getName())) {
            return nameAndStereotype;
        }

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
