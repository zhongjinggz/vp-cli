package vpcli.plugin.domain.vpmodelvisitor.visitor;

public interface VisitorFactory {
    ViElementVisitor under(ViElementVisitor higherVisitor);

    ViElementVisitor createRoot();
}
