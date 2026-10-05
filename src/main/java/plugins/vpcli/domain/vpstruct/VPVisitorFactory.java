package plugins.vpcli.domain.vpstruct;

public interface VPVisitorFactory {
    VPVisitor create(VPVisitor preLevelVisitor);
}
