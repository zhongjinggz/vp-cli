package vpcli.plugin.domain.vpmodelvisitor.element.viproject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import vpcli.plugin.domain.vpmodelvisitor.element.fundation.ViElementFactory;
import vpcli.plugin.drivenadapter.VPProjectRepository;

@Singleton
public class ViProjectFactory {

    private final ViElementFactory viElementFactory;
    private final VPProjectRepository vpProjectRepository;

    @Inject
    public ViProjectFactory(
        ViElementFactory viElementFactory,
        VPProjectRepository vpProjectRepository
    ) {
        this.viElementFactory = viElementFactory;
        this.vpProjectRepository = vpProjectRepository;
    }


    public ViProject build() {
        return new ViProject(vpProjectRepository.get(), viElementFactory);
    }
}

