package vpcli.plugin.domain.vpmodelvisitor.visitor;

public interface ViElementVisitorFactory {
    ViElementVisitor under(ViElementVisitor higherVisitor);

    ViElementVisitor createRoot();
}
