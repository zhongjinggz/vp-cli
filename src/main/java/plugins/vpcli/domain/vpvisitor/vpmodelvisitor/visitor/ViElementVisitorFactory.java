package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

public interface ViElementVisitorFactory {
    ViElementVisitor create(ViElementVisitor preLevelVisitor);
}
