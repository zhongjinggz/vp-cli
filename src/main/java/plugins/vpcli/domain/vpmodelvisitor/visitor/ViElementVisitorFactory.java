package plugins.vpcli.domain.vpmodelvisitor.visitor;

public interface ViElementVisitorFactory {
    ViElementVisitor under(ViElementVisitor higherVisitor);

    ViElementVisitor createRoot();
}
