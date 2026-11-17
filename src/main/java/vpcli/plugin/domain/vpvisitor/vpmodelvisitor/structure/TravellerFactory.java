package vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.vpmodelvisitor.visitor.VisitorFactory;

@Singleton
public class TravellerFactory {

    @Inject
    public TravellerFactory() {
    }

    public Traveller buildTraveller(VisitorFactory visitorFactory, String[] vpElementTypes) {
        return new Traveller(visitorFactory, vpElementTypes);
    }
}
