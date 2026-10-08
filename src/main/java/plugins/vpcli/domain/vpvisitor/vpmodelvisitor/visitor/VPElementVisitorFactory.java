package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

public interface VPElementVisitorFactory {
    VPElementVisitor create(VPElementVisitor preLevelVisitor);
}
