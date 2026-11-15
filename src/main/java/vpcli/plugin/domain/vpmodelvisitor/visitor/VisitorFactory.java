package vpcli.plugin.domain.vpmodelvisitor.visitor;

public interface VisitorFactory {
    Visitor under(Visitor higherVisitor);

    Visitor createRoot();
}
