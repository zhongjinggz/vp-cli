package plugins.vpcli.domain.vpmodelvisitor.element;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.property.IModelProperty;
import com.vp.plugin.model.property.IReferencedBy;

import java.beans.PropertyChangeListener;
import java.util.Iterator;

public class MyClass implements IClass {
    private final IClass vpClass;

    public MyClass(IClass vpClass) {
        this.vpClass = vpClass;
    }

    @Override
    public void addOperation(IOperation iOperation) {
        vpClass.addOperation(iOperation);
    }

    @Override
    public void removeOperation(IOperation iOperation) {
        vpClass.removeOperation(iOperation);
    }

    @Override
    public IOperation getOperationByIndex(int i) {
        return vpClass.getOperationByIndex(i);
    }

    @Override
    public void removeOperationByIndex(int i) {
        vpClass.removeOperationByIndex(i);
    }

    @Override
    public void reorderOperation(IOperation[] iOperations) {
        vpClass.reorderOperation(iOperations);
    }

    @Override
    public int operationCount() {
        return vpClass.operationCount();
    }

    @Override
    public Iterator operationIterator() {
        return vpClass.operationIterator();
    }

    @Override
    public IOperation[] toOperationArray() {
        return vpClass.toOperationArray();
    }

    @Override
    public IOperation createOperation() {
        return vpClass.createOperation();
    }

    @Override
    public IOperation getOperationByName(String s) {
        return vpClass.getOperationByName(s);
    }

    @Override
    public void addAttribute(IAttribute iAttribute) {
        vpClass.addAttribute(iAttribute);
    }

    @Override
    public void removeAttribute(IAttribute iAttribute) {
        vpClass.removeAttribute(iAttribute);
    }

    @Override
    public IAttribute getAttributeByIndex(int i) {
        return vpClass.getAttributeByIndex(i);
    }

    @Override
    public void removeAttributeByIndex(int i) {
        vpClass.removeAttributeByIndex(i);
    }

    @Override
    public void reorderAttribute(IAttribute[] iAttributes) {
        vpClass.reorderAttribute(iAttributes);
    }

    @Override
    public int attributeCount() {
        return vpClass.attributeCount();
    }

    @Override
    public Iterator attributeIterator() {
        return vpClass.attributeIterator();
    }

    @Override
    public IAttribute[] toAttributeArray() {
        return vpClass.toAttributeArray();
    }

    @Override
    public IAttribute createAttribute() {
        return vpClass.createAttribute();
    }

    @Override
    public IAttribute getAttributeByName(String s) {
        return vpClass.getAttributeByName(s);
    }

    @Override
    public void addClass(IClass iClass) {
        vpClass.addClass(iClass);
    }

    @Override
    public void removeClass(IClass iClass) {
        vpClass.removeClass(iClass);
    }

    @Override
    public IClass getClassByIndex(int i) {
        return vpClass.getClassByIndex(i);
    }

    @Override
    public void removeClassByIndex(int i) {
        vpClass.removeClassByIndex(i);
    }

    @Override
    public void reorderClass(IClass[] iClasses) {
        vpClass.reorderClass(iClasses);
    }

    @Override
    public int classCount() {
        return vpClass.classCount();
    }

    @Override
    public Iterator classIterator() {
        return vpClass.classIterator();
    }

    @Override
    public IClass[] toClassArray() {
        return vpClass.toClassArray();
    }

    @Override
    public IClass createClass() {
        return vpClass.createClass();
    }

    @Override
    public IClass getClassByName(String s) {
        return vpClass.getClassByName(s);
    }

    @Override
    public void addPort(IPort iPort) {
        vpClass.addPort(iPort);
    }

    @Override
    public void removePort(IPort iPort) {
        vpClass.removePort(iPort);
    }

    @Override
    public IPort getPortByIndex(int i) {
        return vpClass.getPortByIndex(i);
    }

    @Override
    public void removePortByIndex(int i) {
        vpClass.removePortByIndex(i);
    }

    @Override
    public void reorderPort(IPort[] iPorts) {
        vpClass.reorderPort(iPorts);
    }

    @Override
    public int portCount() {
        return vpClass.portCount();
    }

    @Override
    public Iterator portIterator() {
        return vpClass.portIterator();
    }

    @Override
    public IPort[] toPortArray() {
        return vpClass.toPortArray();
    }

    @Override
    public IPort createPort() {
        return vpClass.createPort();
    }

    @Override
    public IPort getPortByName(String s) {
        return vpClass.getPortByName(s);
    }

    @Override
    public void addStateMachine(IStateMachine iStateMachine) {
        vpClass.addStateMachine(iStateMachine);
    }

    @Override
    public void removeStateMachine(IStateMachine iStateMachine) {
        vpClass.removeStateMachine(iStateMachine);
    }

    @Override
    public IStateMachine getStateMachineByIndex(int i) {
        return vpClass.getStateMachineByIndex(i);
    }

    @Override
    public void removeStateMachineByIndex(int i) {
        vpClass.removeStateMachineByIndex(i);
    }

    @Override
    public void reorderStateMachine(IStateMachine[] iStateMachines) {
        vpClass.reorderStateMachine(iStateMachines);
    }

    @Override
    public int stateMachineCount() {
        return vpClass.stateMachineCount();
    }

    @Override
    public Iterator stateMachineIterator() {
        return vpClass.stateMachineIterator();
    }

    @Override
    public IStateMachine[] toStateMachineArray() {
        return vpClass.toStateMachineArray();
    }

    @Override
    public IStateMachine createStateMachine() {
        return vpClass.createStateMachine();
    }

    @Override
    public IStateMachine getStateMachineByName(String s) {
        return vpClass.getStateMachineByName(s);
    }

    @Override
    public void addActivity(IActivity iActivity) {
        vpClass.addActivity(iActivity);
    }

    @Override
    public void removeActivity(IActivity iActivity) {
        vpClass.removeActivity(iActivity);
    }

    @Override
    public IActivity getActivityByIndex(int i) {
        return vpClass.getActivityByIndex(i);
    }

    @Override
    public void removeActivityByIndex(int i) {
        vpClass.removeActivityByIndex(i);
    }

    @Override
    public void reorderActivity(IActivity[] iActivities) {
        vpClass.reorderActivity(iActivities);
    }

    @Override
    public int activityCount() {
        return vpClass.activityCount();
    }

    @Override
    public Iterator activityIterator() {
        return vpClass.activityIterator();
    }

    @Override
    public IActivity[] toActivityArray() {
        return vpClass.toActivityArray();
    }

    @Override
    public IActivity createActivity() {
        return vpClass.createActivity();
    }

    @Override
    public IActivity getActivityByName(String s) {
        return vpClass.getActivityByName(s);
    }

    @Override
    public void addInteraction(IInteraction iInteraction) {
        vpClass.addInteraction(iInteraction);
    }

    @Override
    public void removeInteraction(IInteraction iInteraction) {
        vpClass.removeInteraction(iInteraction);
    }

    @Override
    public IInteraction getInteractionByIndex(int i) {
        return vpClass.getInteractionByIndex(i);
    }

    @Override
    public void removeInteractionByIndex(int i) {
        vpClass.removeInteractionByIndex(i);
    }

    @Override
    public void reorderInteraction(IInteraction[] iInteractions) {
        vpClass.reorderInteraction(iInteractions);
    }

    @Override
    public int interactionCount() {
        return vpClass.interactionCount();
    }

    @Override
    public Iterator interactionIterator() {
        return vpClass.interactionIterator();
    }

    @Override
    public IInteraction[] toInteractionArray() {
        return vpClass.toInteractionArray();
    }

    @Override
    public IInteraction createInteraction() {
        return vpClass.createInteraction();
    }

    @Override
    public IInteraction getInteractionByName(String s) {
        return vpClass.getInteractionByName(s);
    }

    @Override
    public void addOpaqueBehavior(IOpaqueBehavior iOpaqueBehavior) {
        vpClass.addOpaqueBehavior(iOpaqueBehavior);
    }

    @Override
    public void removeOpaqueBehavior(IOpaqueBehavior iOpaqueBehavior) {
        vpClass.removeOpaqueBehavior(iOpaqueBehavior);
    }

    @Override
    public IOpaqueBehavior getOpaqueBehaviorByIndex(int i) {
        return vpClass.getOpaqueBehaviorByIndex(i);
    }

    @Override
    public void removeOpaqueBehaviorByIndex(int i) {
        vpClass.removeOpaqueBehaviorByIndex(i);
    }

    @Override
    public void reorderOpaqueBehavior(IOpaqueBehavior[] iOpaqueBehaviors) {
        vpClass.reorderOpaqueBehavior(iOpaqueBehaviors);
    }

    @Override
    public int opaqueBehaviorCount() {
        return vpClass.opaqueBehaviorCount();
    }

    @Override
    public Iterator opaqueBehaviorIterator() {
        return vpClass.opaqueBehaviorIterator();
    }

    @Override
    public IOpaqueBehavior[] toOpaqueBehaviorArray() {
        return vpClass.toOpaqueBehaviorArray();
    }

    @Override
    public IOpaqueBehavior createOpaqueBehavior() {
        return vpClass.createOpaqueBehavior();
    }

    @Override
    public IOpaqueBehavior getOpaqueBehaviorByName(String s) {
        return vpClass.getOpaqueBehaviorByName(s);
    }

    @Override
    public void addFunctionBehavior(IFunctionBehavior iFunctionBehavior) {
        vpClass.addFunctionBehavior(iFunctionBehavior);
    }

    @Override
    public void removeFunctionBehavior(IFunctionBehavior iFunctionBehavior) {
        vpClass.removeFunctionBehavior(iFunctionBehavior);
    }

    @Override
    public IFunctionBehavior getFunctionBehaviorByIndex(int i) {
        return vpClass.getFunctionBehaviorByIndex(i);
    }

    @Override
    public void removeFunctionBehaviorByIndex(int i) {
        vpClass.removeFunctionBehaviorByIndex(i);
    }

    @Override
    public void reorderFunctionBehavior(IFunctionBehavior[] iFunctionBehaviors) {
        vpClass.reorderFunctionBehavior(iFunctionBehaviors);
    }

    @Override
    public int functionBehaviorCount() {
        return vpClass.functionBehaviorCount();
    }

    @Override
    public Iterator functionBehaviorIterator() {
        return vpClass.functionBehaviorIterator();
    }

    @Override
    public IFunctionBehavior[] toFunctionBehaviorArray() {
        return vpClass.toFunctionBehaviorArray();
    }

    @Override
    public IFunctionBehavior createFunctionBehavior() {
        return vpClass.createFunctionBehavior();
    }

    @Override
    public IFunctionBehavior getFunctionBehaviorByName(String s) {
        return vpClass.getFunctionBehaviorByName(s);
    }

    @Override
    public void addCollaborationOccurrence(ICollaborationOccurrence iCollaborationOccurrence) {
        vpClass.addCollaborationOccurrence(iCollaborationOccurrence);
    }

    @Override
    public void removeCollaborationOccurrence(ICollaborationOccurrence iCollaborationOccurrence) {
        vpClass.removeCollaborationOccurrence(iCollaborationOccurrence);
    }

    @Override
    public ICollaborationOccurrence getCollaborationOccurrenceByIndex(int i) {
        return vpClass.getCollaborationOccurrenceByIndex(i);
    }

    @Override
    public void removeCollaborationOccurrenceByIndex(int i) {
        vpClass.removeCollaborationOccurrenceByIndex(i);
    }

    @Override
    public void reorderCollaborationOccurrence(ICollaborationOccurrence[] iCollaborationOccurrences) {
        vpClass.reorderCollaborationOccurrence(iCollaborationOccurrences);
    }

    @Override
    public int collaborationOccurrenceCount() {
        return vpClass.collaborationOccurrenceCount();
    }

    @Override
    public Iterator collaborationOccurrenceIterator() {
        return vpClass.collaborationOccurrenceIterator();
    }

    @Override
    public ICollaborationOccurrence[] toCollaborationOccurrenceArray() {
        return vpClass.toCollaborationOccurrenceArray();
    }

    @Override
    public ICollaborationOccurrence createCollaborationOccurrence() {
        return vpClass.createCollaborationOccurrence();
    }

    @Override
    public ICollaborationOccurrence getCollaborationOccurrenceByName(String s) {
        return vpClass.getCollaborationOccurrenceByName(s);
    }

    @Override
    public void addEnumerationLiteral(IEnumerationLiteral iEnumerationLiteral) {
        vpClass.addEnumerationLiteral(iEnumerationLiteral);
    }

    @Override
    public void removeEnumerationLiteral(IEnumerationLiteral iEnumerationLiteral) {
        vpClass.removeEnumerationLiteral(iEnumerationLiteral);
    }

    @Override
    public IEnumerationLiteral getEnumerationLiteralByIndex(int i) {
        return vpClass.getEnumerationLiteralByIndex(i);
    }

    @Override
    public void removeEnumerationLiteralByIndex(int i) {
        vpClass.removeEnumerationLiteralByIndex(i);
    }

    @Override
    public void reorderEnumerationLiteral(IEnumerationLiteral[] iEnumerationLiterals) {
        vpClass.reorderEnumerationLiteral(iEnumerationLiterals);
    }

    @Override
    public int enumerationLiteralCount() {
        return vpClass.enumerationLiteralCount();
    }

    @Override
    public Iterator enumerationLiteralIterator() {
        return vpClass.enumerationLiteralIterator();
    }

    @Override
    public IEnumerationLiteral[] toEnumerationLiteralArray() {
        return vpClass.toEnumerationLiteralArray();
    }

    @Override
    public IEnumerationLiteral createEnumerationLiteral() {
        return vpClass.createEnumerationLiteral();
    }

    @Override
    public IEnumerationLiteral getEnumerationLiteralByName(String s) {
        return vpClass.getEnumerationLiteralByName(s);
    }

    @Override
    public void addReception(IReception iReception) {
        vpClass.addReception(iReception);
    }

    @Override
    public void removeReception(IReception iReception) {
        vpClass.removeReception(iReception);
    }

    @Override
    public IReception getReceptionByIndex(int i) {
        return vpClass.getReceptionByIndex(i);
    }

    @Override
    public void removeReceptionByIndex(int i) {
        vpClass.removeReceptionByIndex(i);
    }

    @Override
    public void reorderReception(IReception[] iReceptions) {
        vpClass.reorderReception(iReceptions);
    }

    @Override
    public int receptionCount() {
        return vpClass.receptionCount();
    }

    @Override
    public Iterator receptionIterator() {
        return vpClass.receptionIterator();
    }

    @Override
    public IReception[] toReceptionArray() {
        return vpClass.toReceptionArray();
    }

    @Override
    public IReception createReception() {
        return vpClass.createReception();
    }

    @Override
    public IReception getReceptionByName(String s) {
        return vpClass.getReceptionByName(s);
    }

    @Override
    public int fromRelationshipCount() {
        return vpClass.fromRelationshipCount();
    }

    @Override
    public ISimpleRelationship[] toFromRelationshipArray() {
        return vpClass.toFromRelationshipArray();
    }

    @Override
    public Iterator fromRelationshipIterator() {
        return vpClass.fromRelationshipIterator();
    }

    @Override
    public int toRelationshipCount() {
        return vpClass.toRelationshipCount();
    }

    @Override
    public ISimpleRelationship[] toToRelationshipArray() {
        return vpClass.toToRelationshipArray();
    }

    @Override
    public Iterator toRelationshipIterator() {
        return vpClass.toRelationshipIterator();
    }

    @Override
    public int fromRelationshipEndCount() {
        return vpClass.fromRelationshipEndCount();
    }

    @Override
    public IRelationshipEnd[] toFromRelationshipEndArray() {
        return vpClass.toFromRelationshipEndArray();
    }

    @Override
    public Iterator fromRelationshipEndIterator() {
        return vpClass.fromRelationshipEndIterator();
    }

    @Override
    public int toRelationshipEndCount() {
        return vpClass.toRelationshipEndCount();
    }

    @Override
    public IRelationshipEnd[] toToRelationshipEndArray() {
        return vpClass.toToRelationshipEndArray();
    }

    @Override
    public Iterator toRelationshipEndIterator() {
        return vpClass.toRelationshipEndIterator();
    }

    @Override
    public void addOrmQualifier(IORMQualifier iormQualifier) {
        vpClass.addOrmQualifier(iormQualifier);
    }

    @Override
    public void removeOrmQualifier(IORMQualifier iormQualifier) {
        vpClass.removeOrmQualifier(iormQualifier);
    }

    @Override
    public IORMQualifier getOrmQualifierByIndex(int i) {
        return vpClass.getOrmQualifierByIndex(i);
    }

    @Override
    public void removeOrmQualifierByIndex(int i) {
        vpClass.removeOrmQualifierByIndex(i);
    }

    @Override
    public int ormQualifierCount() {
        return vpClass.ormQualifierCount();
    }

    @Override
    public Iterator ormQualifierIterator() {
        return vpClass.ormQualifierIterator();
    }

    @Override
    public IORMQualifier[] toOrmQualifierArray() {
        return vpClass.toOrmQualifierArray();
    }

    @Override
    public void setActive(boolean b) {
        vpClass.setActive(b);
    }

    @Override
    public boolean isActive() {
        return vpClass.isActive();
    }

    @Override
    public void setBusinessKeyMutable(boolean b) {
        vpClass.setBusinessKeyMutable(b);
    }

    @Override
    public boolean isBusinessKeyMutable() {
        return vpClass.isBusinessKeyMutable();
    }

    @Override
    public void setJavaDetail(IJavaClassCodeDetail iJavaClassCodeDetail) {
        vpClass.setJavaDetail(iJavaClassCodeDetail);
    }

    @Override
    public IJavaClassCodeDetail getJavaDetail() {
        return vpClass.getJavaDetail();
    }

    @Override
    public void setOrmDetail(IORMClassDetail iormClassDetail) {
        vpClass.setOrmDetail(iormClassDetail);
    }

    @Override
    public IORMClassDetail getOrmDetail() {
        return vpClass.getOrmDetail();
    }

    @Override
    public void setDotnetCodeDetail(IDotNetClassCodeDetail iDotNetClassCodeDetail) {
        vpClass.setDotnetCodeDetail(iDotNetClassCodeDetail);
    }

    @Override
    public IDotNetClassCodeDetail getDotnetCodeDetail() {
        return vpClass.getDotnetCodeDetail();
    }

    @Override
    public void setActionScriptCodeDetail(IActionScriptClassCodeDetail iActionScriptClassCodeDetail) {
        vpClass.setActionScriptCodeDetail(iActionScriptClassCodeDetail);
    }

    @Override
    public IActionScriptClassCodeDetail getActionScriptCodeDetail() {
        return vpClass.getActionScriptCodeDetail();
    }

    @Override
    public void setEjbCodeDetail(IEJBClassCodeDetail iejbClassCodeDetail) {
        vpClass.setEjbCodeDetail(iejbClassCodeDetail);
    }

    @Override
    public IEJBClassCodeDetail getEjbCodeDetail() {
        return vpClass.getEjbCodeDetail();
    }

    @Override
    public void addTemplateParameter(ITemplateParameter iTemplateParameter) {
        vpClass.addTemplateParameter(iTemplateParameter);
    }

    @Override
    public void removeTemplateParameter(ITemplateParameter iTemplateParameter) {
        vpClass.removeTemplateParameter(iTemplateParameter);
    }

    @Override
    public ITemplateParameter getTemplateParameterByIndex(int i) {
        return vpClass.getTemplateParameterByIndex(i);
    }

    @Override
    public void removeTemplateParameterByIndex(int i) {
        vpClass.removeTemplateParameterByIndex(i);
    }

    @Override
    public int templateParameterCount() {
        return vpClass.templateParameterCount();
    }

    @Override
    public Iterator templateParameterIterator() {
        return vpClass.templateParameterIterator();
    }

    @Override
    public ITemplateParameter[] toTemplateParameterArray() {
        return vpClass.toTemplateParameterArray();
    }

    @Override
    public void setBusinessModel(boolean b) {
        vpClass.setBusinessModel(b);
    }

    @Override
    public boolean isBusinessModel() {
        return vpClass.isBusinessModel();
    }

    @Override
    public void setVisibility(String s) {
        vpClass.setVisibility(s);
    }

    @Override
    public String getVisibility() {
        return vpClass.getVisibility();
    }

    @Override
    public void setFinalSpecialization(boolean b) {
        vpClass.setFinalSpecialization(b);
    }

    @Override
    public boolean isFinalSpecialization() {
        return vpClass.isFinalSpecialization();
    }

    @Override
    public void setAbstract(boolean b) {
        vpClass.setAbstract(b);
    }

    @Override
    public boolean isAbstract() {
        return vpClass.isAbstract();
    }

    @Override
    public void setLeaf(boolean b) {
        vpClass.setLeaf(b);
    }

    @Override
    public boolean isLeaf() {
        return vpClass.isLeaf();
    }

    @Override
    public void setRoot(boolean b) {
        vpClass.setRoot(b);
    }

    @Override
    public boolean isRoot() {
        return vpClass.isRoot();
    }

    @Override
    public void addStereotype(String s) {
        vpClass.addStereotype(s);
    }

    @Override
    public void removeStereotype(String s) {
        vpClass.removeStereotype(s);
    }

    @Override
    public void addStereotype(IStereotype iStereotype) {
        vpClass.addStereotype(iStereotype);
    }

    @Override
    public void removeStereotype(IStereotype iStereotype) {
        vpClass.removeStereotype(iStereotype);
    }

    @Override
    public boolean hasStereotype(String s) {
        return vpClass.hasStereotype(s);
    }

    @Override
    public int stereotypeCount() {
        return vpClass.stereotypeCount();
    }

    @Override
    public String[] toStereotypeArray() {
        return vpClass.toStereotypeArray();
    }

    @Override
    public Iterator stereotypeIterator() {
        return vpClass.stereotypeIterator();
    }

    @Override
    public Iterator stereotypeModelIterator() {
        return vpClass.stereotypeModelIterator();
    }

    @Override
    public IStereotype[] toStereotypeModelArray() {
        return vpClass.toStereotypeModelArray();
    }

    @Override
    public void addStereotypes(String s) {
        vpClass.addStereotypes(s);
    }

    @Override
    public void removeStereotypes(String s) {
        vpClass.removeStereotypes(s);
    }

    @Override
    public boolean hasStereotypes(String s) {
        return vpClass.hasStereotypes(s);
    }

    @Override
    public int stereotypesCount() {
        return vpClass.stereotypesCount();
    }

    @Override
    public String[] toStereotypesArray() {
        return vpClass.toStereotypesArray();
    }

    @Override
    public Iterator stereotypesIterator() {
        return vpClass.stereotypesIterator();
    }

    @Override
    public Iterator stereotypesModelIterator() {
        return vpClass.stereotypesModelIterator();
    }

    @Override
    public IStereotype[] toStereotypesModelArray() {
        return vpClass.toStereotypesModelArray();
    }

    @Override
    public void setTaggedValues(ITaggedValueContainer iTaggedValueContainer) {
        vpClass.setTaggedValues(iTaggedValueContainer);
    }

    @Override
    public ITaggedValueContainer getTaggedValues() {
        return vpClass.getTaggedValues();
    }

    @Override
    public void addSyncMappingModel(IModelElement iModelElement) {
        vpClass.addSyncMappingModel(iModelElement);
    }

    @Override
    public void removeSyncMappingModel(IModelElement iModelElement) {
        vpClass.removeSyncMappingModel(iModelElement);
    }

    @Override
    public IModelElement getSyncMappingModelByIndex(int i) {
        return vpClass.getSyncMappingModelByIndex(i);
    }

    @Override
    public void removeSyncMappingModelByIndex(int i) {
        vpClass.removeSyncMappingModelByIndex(i);
    }

    @Override
    public int syncMappingModelCount() {
        return vpClass.syncMappingModelCount();
    }

    @Override
    public Iterator syncMappingModelIterator() {
        return vpClass.syncMappingModelIterator();
    }

    @Override
    public IModelElement[] toSyncMappingModelArray() {
        return vpClass.toSyncMappingModelArray();
    }

    @Override
    public void addAnalysisItemModel(IModelElement iModelElement) {
        vpClass.addAnalysisItemModel(iModelElement);
    }

    @Override
    public void removeAnalysisItemModel(IModelElement iModelElement) {
        vpClass.removeAnalysisItemModel(iModelElement);
    }

    @Override
    public IModelElement getAnalysisItemModelByIndex(int i) {
        return vpClass.getAnalysisItemModelByIndex(i);
    }

    @Override
    public void removeAnalysisItemModelByIndex(int i) {
        vpClass.removeAnalysisItemModelByIndex(i);
    }

    @Override
    public int analysisItemModelCount() {
        return vpClass.analysisItemModelCount();
    }

    @Override
    public Iterator analysisItemModelIterator() {
        return vpClass.analysisItemModelIterator();
    }

    @Override
    public IModelElement[] toAnalysisItemModelArray() {
        return vpClass.toAnalysisItemModelArray();
    }

    @Override
    public void setAnalysisItemDiagramIds(String[] strings) {
        vpClass.setAnalysisItemDiagramIds(strings);
    }

    @Override
    public String[] getAnalysisItemDiagramIds() {
        return vpClass.getAnalysisItemDiagramIds();
    }

    @Override
    public void setQualityScore(int i) {
        vpClass.setQualityScore(i);
    }

    @Override
    public int getQualityScore() {
        return vpClass.getQualityScore();
    }

    @Override
    public void setQualityReason(String s) {
        vpClass.setQualityReason(s);
    }

    @Override
    public String getQualityReason() {
        return vpClass.getQualityReason();
    }

    @Override
    public void setTransitFrom(String s) {
        vpClass.setTransitFrom(s);
    }

    @Override
    public String getTransitFrom() {
        return vpClass.getTransitFrom();
    }

    @Override
    public void setTransitTo(String s) {
        vpClass.setTransitTo(s);
    }

    @Override
    public String getTransitTo() {
        return vpClass.getTransitTo();
    }

    @Override
    public void addComment(IComment iComment) {
        vpClass.addComment(iComment);
    }

    @Override
    public void removeComment(IComment iComment) {
        vpClass.removeComment(iComment);
    }

    @Override
    public IComment getCommentByIndex(int i) {
        return vpClass.getCommentByIndex(i);
    }

    @Override
    public void removeCommentByIndex(int i) {
        vpClass.removeCommentByIndex(i);
    }

    @Override
    public int commentCount() {
        return vpClass.commentCount();
    }

    @Override
    public Iterator commentIterator() {
        return vpClass.commentIterator();
    }

    @Override
    public IComment[] toCommentArray() {
        return vpClass.toCommentArray();
    }

    @Override
    public void setDescription(String s) {
        vpClass.setDescription(s);
    }

    @Override
    public String getDescription() {
        return vpClass.getDescription();
    }

    @Override
    public void setHTMLDescription(String s) {
        vpClass.setHTMLDescription(s);
    }

    @Override
    public String getHTMLDescription() {
        return vpClass.getHTMLDescription();
    }

    @Override
    public String getHTMLDescription(boolean b) {
        return vpClass.getHTMLDescription(b);
    }

    @Override
    public void addVoice(IVoice iVoice) {
        vpClass.addVoice(iVoice);
    }

    @Override
    public void removeVoice(IVoice iVoice) {
        vpClass.removeVoice(iVoice);
    }

    @Override
    public IVoice getVoiceByIndex(int i) {
        return vpClass.getVoiceByIndex(i);
    }

    @Override
    public void removeVoiceByIndex(int i) {
        vpClass.removeVoiceByIndex(i);
    }

    @Override
    public int voiceCount() {
        return vpClass.voiceCount();
    }

    @Override
    public Iterator voiceIterator() {
        return vpClass.voiceIterator();
    }

    @Override
    public IVoice[] toVoiceArray() {
        return vpClass.toVoiceArray();
    }

    @Override
    public void addReference(IReference iReference) {
        vpClass.addReference(iReference);
    }

    @Override
    public void removeReference(IReference iReference) {
        vpClass.removeReference(iReference);
    }

    @Override
    public IReference getReferenceByIndex(int i) {
        return vpClass.getReferenceByIndex(i);
    }

    @Override
    public void removeReferenceByIndex(int i) {
        vpClass.removeReferenceByIndex(i);
    }

    @Override
    public int referenceCount() {
        return vpClass.referenceCount();
    }

    @Override
    public Iterator referenceIterator() {
        return vpClass.referenceIterator();
    }

    @Override
    public IReference[] toReferenceArray() {
        return vpClass.toReferenceArray();
    }

    @Override
    public void setReferencedByDiagramIds(String[] strings) {
        vpClass.setReferencedByDiagramIds(strings);
    }

    @Override
    public String[] getReferencedByDiagramIds() {
        return vpClass.getReferencedByDiagramIds();
    }

    @Override
    public void setReferencedByModelAddresses(String[] strings) {
        vpClass.setReferencedByModelAddresses(strings);
    }

    @Override
    public String[] getReferencedByModelAddresses() {
        return vpClass.getReferencedByModelAddresses();
    }

    @Override
    public void setPmStatus(IPMStatus ipmStatus) {
        vpClass.setPmStatus(ipmStatus);
    }

    @Override
    public IPMStatus getPmStatus() {
        return vpClass.getPmStatus();
    }

    @Override
    public void setPmDifficulty(IPMDifficulty ipmDifficulty) {
        vpClass.setPmDifficulty(ipmDifficulty);
    }

    @Override
    public IPMDifficulty getPmDifficulty() {
        return vpClass.getPmDifficulty();
    }

    @Override
    public void setPmPriority(IPMPriority ipmPriority) {
        vpClass.setPmPriority(ipmPriority);
    }

    @Override
    public IPMPriority getPmPriority() {
        return vpClass.getPmPriority();
    }

    @Override
    public void setPmVersion(IPMVersion ipmVersion) {
        vpClass.setPmVersion(ipmVersion);
    }

    @Override
    public IPMVersion getPmVersion() {
        return vpClass.getPmVersion();
    }

    @Override
    public void setPmIteration(IPMIteration ipmIteration) {
        vpClass.setPmIteration(ipmIteration);
    }

    @Override
    public IPMIteration getPmIteration() {
        return vpClass.getPmIteration();
    }

    @Override
    public void setPmPhase(IPMPhase ipmPhase) {
        vpClass.setPmPhase(ipmPhase);
    }

    @Override
    public IPMPhase getPmPhase() {
        return vpClass.getPmPhase();
    }

    @Override
    public void setPmDiscipline(IPMDiscipline ipmDiscipline) {
        vpClass.setPmDiscipline(ipmDiscipline);
    }

    @Override
    public IPMDiscipline getPmDiscipline() {
        return vpClass.getPmDiscipline();
    }

    @Override
    public void setPmAuthor(String s) {
        vpClass.setPmAuthor(s);
    }

    @Override
    public String getPmAuthor() {
        return vpClass.getPmAuthor();
    }

    @Override
    public void setPmCreateDateTime(String s) {
        vpClass.setPmCreateDateTime(s);
    }

    @Override
    public String getPmCreateDateTime() {
        return vpClass.getPmCreateDateTime();
    }

    @Override
    public void setPmLastModified(String s) {
        vpClass.setPmLastModified(s);
    }

    @Override
    public String getPmLastModified() {
        return vpClass.getPmLastModified();
    }

    @Override
    public void setUserID(String s) {
        vpClass.setUserID(s);
    }

    @Override
    public String getUserID() {
        return vpClass.getUserID();
    }

    @Override
    public void setUserIDParent(String s) {
        vpClass.setUserIDParent(s);
    }

    @Override
    public String getUserIDParent() {
        return vpClass.getUserIDParent();
    }

    @Override
    public void setUserIDLastNumericValue(int i) {
        vpClass.setUserIDLastNumericValue(i);
    }

    @Override
    public int getUserIDLastNumericValue() {
        return vpClass.getUserIDLastNumericValue();
    }

    @Override
    public void setBacklogActivityId(int i) {
        vpClass.setBacklogActivityId(i);
    }

    @Override
    public int getBacklogActivityId() {
        return vpClass.getBacklogActivityId();
    }

    @Override
    public void addGenericValue(IGenericValue iGenericValue) {
        vpClass.addGenericValue(iGenericValue);
    }

    @Override
    public void removeGenericValue(IGenericValue iGenericValue) {
        vpClass.removeGenericValue(iGenericValue);
    }

    @Override
    public IGenericValue getGenericValueByIndex(int i) {
        return vpClass.getGenericValueByIndex(i);
    }

    @Override
    public void removeGenericValueByIndex(int i) {
        vpClass.removeGenericValueByIndex(i);
    }

    @Override
    public int genericValueCount() {
        return vpClass.genericValueCount();
    }

    @Override
    public Iterator genericValueIterator() {
        return vpClass.genericValueIterator();
    }

    @Override
    public IGenericValue[] toGenericValueArray() {
        return vpClass.toGenericValueArray();
    }

    @Override
    public String getId() {
        return vpClass.getId();
    }

    @Override
    public String getModelType() {
        return vpClass.getModelType();
    }

    @Override
    public String getAddress() {
        return vpClass.getAddress();
    }

    @Override
    public IModelElement getParent() {
        return vpClass.getParent();
    }

    @Override
    public void setName(String s) {
        vpClass.setName(s);
    }

    @Override
    public String getName() {
        return vpClass.getName();
    }

    @Override
    public void setNickname(String s) {
        vpClass.setNickname(s);
    }

    @Override
    public String getNickname() {
        return vpClass.getNickname();
    }

    @Override
    public String getNickPropertyValue(String s) {
        return vpClass.getNickPropertyValue(s);
    }

    @Override
    public void addChild(IModelElement iModelElement) {
        vpClass.addChild(iModelElement);
    }

    @Override
    public void removeChild(IModelElement iModelElement) {
        vpClass.removeChild(iModelElement);
    }

    @Override
    public IModelElement getChildByIndex(int i) {
        return vpClass.getChildByIndex(i);
    }

    @Override
    public IModelElement getChildByIndex(String s, int i) {
        return vpClass.getChildByIndex(s, i);
    }

    @Override
    public void removeChildByIndex(int i) {
        vpClass.removeChildByIndex(i);
    }

    @Override
    public int childCount() {
        return vpClass.childCount();
    }

    @Override
    public int childCount(String s) {
        return vpClass.childCount(s);
    }

    @Override
    public int childCount(String[] strings) {
        return vpClass.childCount(strings);
    }

    @Override
    public Iterator childIterator() {
        return vpClass.childIterator();
    }

    @Override
    public Iterator childIterator(String s) {
        return vpClass.childIterator(s);
    }

    @Override
    public Iterator childIterator(String[] strings) {
        return vpClass.childIterator(strings);
    }

    @Override
    public IModelElement[] toChildArray() {
        return vpClass.toChildArray();
    }

    @Override
    public IModelElement[] toChildArray(String s) {
        return vpClass.toChildArray(s);
    }

    @Override
    public IModelElement[] toChildArray(String[] strings) {
        return vpClass.toChildArray(strings);
    }

    @Override
    public IModelElement createChild(String s) {
        return vpClass.createChild(s);
    }

    @Override
    public IModelElement getChildById(String s) {
        return vpClass.getChildById(s);
    }

    @Override
    public IModelElement getChildByName(String s) {
        return vpClass.getChildByName(s);
    }

    @Override
    public IModelElement getChildByName(String s, String s1) {
        return vpClass.getChildByName(s, s1);
    }

    @Override
    public int fromChartRelationshipCount() {
        return vpClass.fromChartRelationshipCount();
    }

    @Override
    public IChartRelationship[] toFromChartRelationshipArray() {
        return vpClass.toFromChartRelationshipArray();
    }

    @Override
    public Iterator fromChartRelationshipIterator() {
        return vpClass.fromChartRelationshipIterator();
    }

    @Override
    public int toChartRelationshipCount() {
        return vpClass.toChartRelationshipCount();
    }

    @Override
    public IChartRelationship[] toToChartRelationshipArray() {
        return vpClass.toToChartRelationshipArray();
    }

    @Override
    public Iterator toChartRelationshipIterator() {
        return vpClass.toChartRelationshipIterator();
    }

    @Override
    public void addSubDiagram(IDiagramUIModel iDiagramUIModel) {
        vpClass.addSubDiagram(iDiagramUIModel);
    }

    @Override
    public void removeSubDiagram(IDiagramUIModel iDiagramUIModel) {
        vpClass.removeSubDiagram(iDiagramUIModel);
    }

    @Override
    public IDiagramUIModel getSubDiagramAt(int i) {
        return vpClass.getSubDiagramAt(i);
    }

    @Override
    public int indexOfSubDiagram(IDiagramUIModel iDiagramUIModel) {
        return vpClass.indexOfSubDiagram(iDiagramUIModel);
    }

    @Override
    public int subDiagramCount() {
        return vpClass.subDiagramCount();
    }

    @Override
    public Iterator subDiagramIterator() {
        return vpClass.subDiagramIterator();
    }

    @Override
    public IDiagramUIModel[] toSubDiagramArray() {
        return vpClass.toSubDiagramArray();
    }

    @Override
    public void setDocumentation(String s) {
        vpClass.setDocumentation(s);
    }

    @Override
    public void setHTMLDocumentation(String s) {
        vpClass.setHTMLDocumentation(s);
    }

    @Override
    public String getDocumentation() {
        return vpClass.getDocumentation();
    }

    @Override
    public String getHTMLDocumentation() {
        return vpClass.getHTMLDocumentation();
    }

    @Override
    public String getHTMLDocumentation(boolean b) {
        return vpClass.getHTMLDocumentation(b);
    }

    @Override
    public String getDocumentationWithReferenceModels() {
        return vpClass.getDocumentationWithReferenceModels();
    }

    @Override
    public String getHTMLDocumentationWithReferenceModels() {
        return vpClass.getHTMLDocumentationWithReferenceModels();
    }

    @Override
    public String getHTMLDocumentationWithReferenceModels(boolean b) {
        return vpClass.getHTMLDocumentationWithReferenceModels(b);
    }

    @Override
    public void setNickDocumentation(String s) {
        vpClass.setNickDocumentation(s);
    }

    @Override
    public void setNickHTMLDocumentation(String s) {
        vpClass.setNickHTMLDocumentation(s);
    }

    @Override
    public String getNickDocumentation() {
        return vpClass.getNickDocumentation();
    }

    @Override
    public String getNickHTMLDocumentation() {
        return vpClass.getNickHTMLDocumentation();
    }

    @Override
    public String getNickHTMLDocumentation(boolean b) {
        return vpClass.getNickHTMLDocumentation(b);
    }

    @Override
    public String getNickDocumentationWithReferenceModels() {
        return vpClass.getNickDocumentationWithReferenceModels();
    }

    @Override
    public String getNickHTMLDocumentationWithReferenceModels() {
        return vpClass.getNickHTMLDocumentationWithReferenceModels();
    }

    @Override
    public String getNickHTMLDocumentationWithReferenceModels(boolean b) {
        return vpClass.getNickHTMLDocumentationWithReferenceModels(b);
    }

    @Override
    public String getDescriptionWithReferenceModels() {
        return vpClass.getDescriptionWithReferenceModels();
    }

    @Override
    public String getHTMLDescriptionWithReferenceModels() {
        return vpClass.getHTMLDescriptionWithReferenceModels();
    }

    @Override
    public String getHTMLDescriptionWithReferenceModels(boolean b) {
        return vpClass.getHTMLDescriptionWithReferenceModels(b);
    }

    @Override
    public void setNickDescription(String s) {
        vpClass.setNickDescription(s);
    }

    @Override
    public void setNickHTMLDescription(String s) {
        vpClass.setNickHTMLDescription(s);
    }

    @Override
    public String getNickDescription() {
        return vpClass.getNickDescription();
    }

    @Override
    public String getNickHTMLDescription() {
        return vpClass.getNickHTMLDescription();
    }

    @Override
    public String getNickHTMLDescription(boolean b) {
        return vpClass.getNickHTMLDescription(b);
    }

    @Override
    public String getNickDescriptionWithReferenceModels() {
        return vpClass.getNickDescriptionWithReferenceModels();
    }

    @Override
    public String getNickHTMLDescriptionWithReferenceModels() {
        return vpClass.getNickHTMLDescriptionWithReferenceModels();
    }

    @Override
    public String getNickHTMLDescriptionWithReferenceModels(boolean b) {
        return vpClass.getNickHTMLDescriptionWithReferenceModels(b);
    }

    @Override
    public void delete() {
        vpClass.delete();
    }

    @Override
    public IModelProperty getModelPropertyByName(String s) {
        return vpClass.getModelPropertyByName(s);
    }

    @Override
    public IModelProperty[] toModelPropertyArray() {
        return vpClass.toModelPropertyArray();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        vpClass.addPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        vpClass.removePropertyChangeListener(propertyChangeListener);
    }

    @Override
    public IDiagramElement[] getDiagramElements() {
        return vpClass.getDiagramElements();
    }

    @Override
    public IDiagramElement getMasterView() {
        return vpClass.getMasterView();
    }

    @Override
    public IModelElement[] getReferencingModels(String s, String s1) {
        return vpClass.getReferencingModels(s, s1);
    }

    @Override
    public IReferencedBy[] getReferencedBys() {
        return vpClass.getReferencedBys();
    }

    @Override
    public String getDisplayQualityScore() {
        return vpClass.getDisplayQualityScore();
    }

    @Override
    public IModelQualityDetail[] getModelQualityDetails() {
        return vpClass.getModelQualityDetails();
    }

    @Override
    public IModelElement duplicate() {
        return vpClass.duplicate();
    }

    @Override
    public Iterator constraintsIterator() {
        return vpClass.constraintsIterator();
    }

    @Override
    public String toPropertiesString() {
        return vpClass.toPropertiesString();
    }

    @Override
    public IProject getProject() {
        return vpClass.getProject();
    }

    @Override
    public boolean isTransientModel() {
        return vpClass.isTransientModel();
    }

    @Override
    public void disposeTransientModel() {
        vpClass.disposeTransientModel();
    }

    @Override
    public boolean isMirrorModelElement() {
        return vpClass.isMirrorModelElement();
    }

    @Override
    public IModelElement getMirrorSource() {
        return vpClass.getMirrorSource();
    }

    @Override
    public boolean canModify() {
        return vpClass.canModify();
    }
}
