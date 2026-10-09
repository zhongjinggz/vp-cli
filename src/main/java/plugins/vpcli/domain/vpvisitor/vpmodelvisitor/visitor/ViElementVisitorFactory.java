package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

public interface ViElementVisitorFactory {
    ViElementVisitor under(ViElementVisitor higherVisitor);

    ViElementVisitor createRoot();
}
