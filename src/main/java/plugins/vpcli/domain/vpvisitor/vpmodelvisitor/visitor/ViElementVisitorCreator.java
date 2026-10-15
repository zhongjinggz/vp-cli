package plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor;

public interface ViElementVisitorCreator {
    ViElementVisitor under(ViElementVisitor higherVisitor);

    ViElementVisitor createRoot();
}
