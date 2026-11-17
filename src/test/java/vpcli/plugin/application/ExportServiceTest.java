package vpcli.plugin.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.model.IProject;
import java.io.File;
import org.junit.jupiter.api.Test;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProject;
import vpcli.plugin.domain.vpmodelvisitor.structure.ViProjectFactory;
import vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor.ExportDiagramVisitorFactory;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.Traveller;
import vpcli.plugin.domain.vpvisitor.vpmodelvisitor.structure.TravellerFactory;
import vpcli.plugin.drivenadapter.VPProjectRepository;

/**
 * 验证 ExportService 改用 Traveller 后行为不变：
 * exportLocation 被冻结到 ExportDiagramVisitorFactory（setRootDir），
 * 随后以 MODEL/PACKAGE 类型过滤经 Traveller 遍历项目。
 */
class ExportServiceTest {

    @Test
    void exportAll_freezesRootDirThenTravelsProject() throws Exception {
        VPProjectRepository repo = mock(VPProjectRepository.class);
        IProject vpProject = mock(IProject.class);
        when(repo.get()).thenReturn(vpProject);

        ViProjectFactory viProjectFactory = mock(ViProjectFactory.class);
        ViProject viProject = mock(ViProject.class);
        when(viProjectFactory.build(any(IProject.class))).thenReturn(viProject);

        ExportDiagramVisitorFactory exportFactory = mock(ExportDiagramVisitorFactory.class);
        TravellerFactory travellerFactory = mock(TravellerFactory.class);
        Traveller traveller = mock(Traveller.class);
        when(travellerFactory.buildTraveller(any(), any())).thenReturn(traveller);

        ExportService service = new ExportService(
            repo, viProjectFactory, exportFactory, travellerFactory);

        File exportLocation = new File("/tmp/out");
        service.exportAll(exportLocation);

        verify(exportFactory).setRootDir(exportLocation);
        verify(traveller).travel(viProject);
    }

    @Test
    void exportAll_buildsTravellerWithExportFactoryAndModelPackageTypes() throws Exception {
        VPProjectRepository repo = mock(VPProjectRepository.class);
        IProject vpProject = mock(IProject.class);
        when(repo.get()).thenReturn(vpProject);

        ViProjectFactory viProjectFactory = mock(ViProjectFactory.class);
        when(viProjectFactory.build(any(IProject.class))).thenReturn(mock(ViProject.class));

        ExportDiagramVisitorFactory exportFactory = mock(ExportDiagramVisitorFactory.class);
        TravellerFactory travellerFactory = mock(TravellerFactory.class);
        Traveller traveller = mock(Traveller.class);
        when(travellerFactory.buildTraveller(any(), any())).thenReturn(traveller);

        ExportService service = new ExportService(
            repo, viProjectFactory, exportFactory, travellerFactory);

        service.exportAll(new File("/tmp/out2"));

        verify(travellerFactory).buildTraveller(same(exportFactory), any(String[].class));
    }
}
