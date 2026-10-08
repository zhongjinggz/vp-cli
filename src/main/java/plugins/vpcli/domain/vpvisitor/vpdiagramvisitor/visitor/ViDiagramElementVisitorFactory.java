package plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.visitor;

public interface ViDiagramElementVisitorFactory {
    ViDiagramElementVisitor create(ViDiagramElementVisitor preLevelVisitor);
}
