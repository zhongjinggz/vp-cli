package plugins.vpcli.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.shape.IQualifierUIModel;
import com.vp.plugin.model.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlDiagram;
import plugins.vpcli.domain.outputmodel.pumldiagram.PumlEdgeEnd.ShapeKind;
import plugins.vpcli.domain.outputmodel.pumlexporter.PumlElementExporter;
import plugins.vpcli.util.Strings;

import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_QUALIFIER;
import static plugins.vpcli.util.Strings.*;

@Singleton
public class PumlAssociationExporter extends PumlElementExporter {
    Logger log = LoggerFactory.getLogger(PumlAssociationExporter.class);

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
            Strings.buildStringPatcher(fromAlias(vpDiagramElement, association))
                .patch(fromQualifier(association))
                .patch(fromMultiplicityAndRole(association))
                .patch(associationLine(association))
                .patch(ToMultiplicityAndRole(association))
                .patch(toQualifier(association))
                .patch(toAlias(vpDiagramElement, association))
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
        IAssociationEnd associationEnd = (IAssociationEnd) relationshipEnd;

        var result = Strings.buildStringPatcher()
            .patch(associationEnd.getName())
            .patch(normalizeMultiplicity(associationEnd.getMultiplicity()))
            .toString();

        if (isNotBlank(result)) {
            result = "\"" + result + "\"";
        }

        return result;
    }

    private @NotNull String fromQualifier(IAssociation association) {
        return getQualifier((IAssociationEnd) association.getFromEnd());
    }

    private @NotNull String toQualifier(IAssociation association) {
        return getQualifier((IAssociationEnd) association.getToEnd());
    }

    private String getQualifier(IAssociationEnd associationEnd) {

        IQualifier qualifier = associationEnd.getQualifier();
        if (qualifier == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (IAttribute attribute : qualifier.toAttributeArray()) {
            if (isNotBlank(attribute.getName())) {
                if (result.length() > 0) {
                    result.append("\\n");
                }
                result.append(attribute.getName());
            }
        }
        if (result.length() > 0) {
            result.insert(0, "[").append("]");
        }

        return result.toString();
    }

    private String fromAlias(IDiagramElement vpDiagramElement, IAssociation association) {
        IConnectorUIModel connector = vpDiagramElement instanceof IConnectorUIModel
            ? (IConnectorUIModel) vpDiagramElement
            : null;
        if (connector != null && connector.getFromShape() != null
            && connector.getFromShape().getModelElement() != null) {

            debugQualifier(connector.getFromShape());


            IDiagramElement fromShape = connector.getFromShape();
            if (Objects.equals(fromShape.getShapeType(), SHAPE_TYPE_QUALIFIER)) {
                fromShape = ((IQualifierUIModel) fromShape).getParent();
            }


            return pumlAlias(fromShape.getModelElement(), fromShape.getId());
        }
        return pumlAlias(association.getFromEnd().getModelElement(), null);
    }

    private void debugQualifier(IShapeUIModel theQualifier) {
        if (theQualifier instanceof IQualifierUIModel) {
            var qualifierModelElement = theQualifier.getModelElement();
            log.debug("Debug Qualifier: qualifierShapeId={}; qualifierModelId={};", theQualifier.getId(), qualifierModelElement.getId());

            var parent = theQualifier.getParent();
            if (parent != null) {
                var parentModelElement = parent.getModelElement();
                String parentModelType;
                String parentModelId;
                if (parentModelElement != null) {
                    parentModelType = parentModelElement.getModelType();
                    parentModelId = parentModelElement.getId();
                } else {
                    parentModelType = "no-parent-model";
                    parentModelId = "no-parent-model";
                }
                log.debug("Debug Qualifier: parentShapeType={}; parentId={}; parentModelType={}; parentModelId={}",
                    parent.getShapeType(), parent.getId(), parentModelType, parentModelId);
            }

            var groupParent = theQualifier.getGroupParent();
            if (groupParent != null) {
                var groupParentModelElement = groupParent.getModelElement();
                String groupParentModelType;
                String groupParentModelId;
                if (groupParentModelElement != null) {
                    groupParentModelType = groupParentModelElement.getModelType();
                    groupParentModelId = groupParentModelElement.getId();
                } else {
                    groupParentModelType = "no-group-parent-model";
                    groupParentModelId = "no-group-parent-model";
                }
                log.debug("Debug Qualifier: groupParentShapeType={}; groupParentId={}; groupParentModelType={}; groupParentModelId={}",
                    groupParent.getShapeType(), groupParent.getId(), groupParentModelType, groupParentModelId);
            }
        }
    }

    private String toAlias(IDiagramElement vpDiagramElement, IAssociation association) {
        IConnectorUIModel connector = vpDiagramElement instanceof IConnectorUIModel
            ? (IConnectorUIModel) vpDiagramElement
            : null;
        if (connector != null && connector.getToShape() != null
            && connector.getToShape().getModelElement() != null) {

            debugQualifier(connector.getToShape());

            IDiagramElement toShape = connector.getToShape();
            if (Objects.equals(toShape.getShapeType(), SHAPE_TYPE_QUALIFIER)) {
                toShape = ((IQualifierUIModel) toShape).getParent();
            }


            return pumlAlias(toShape.getModelElement(), toShape.getId());
        }
        return pumlAlias(association.getToEnd().getModelElement(), null);
    }

    private @NonNull String labels(IAssociation association) {

        var nameAndStereotypes = buildStringPatcher()
            .patch(association.getName())
            .patch(getStereotypesText(association));

        var result = patchDirection(association, nameAndStereotypes);

        if (result.isNotBlank()) {
            result.patchBefore(":");
        }
        return result.toString();
    }

    private static StringPatcher patchDirection(IAssociation association, StringPatcher nameAndStereotypes) {
        if (isNotBlank(association.getName())) {
            switch (association.getDirection()) {
                case IAssociation.DIRECTION_FROM_TO:
                    nameAndStereotypes = nameAndStereotypes.patch(">");
                    break;
                case IAssociation.DIRECTION_TO_FROM:
                    nameAndStereotypes = nameAndStereotypes.patchBefore("<");
                    break;
            }
        }
        return nameAndStereotypes;
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
