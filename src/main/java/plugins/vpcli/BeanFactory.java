package plugins.vpcli;

import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.writers_old.WriterCreator;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorCreator;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorCreator;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;
import plugins.vpcli.domain.outputmodel.PumlDiagramCreator;
import plugins.vpcli.domain.outputmodel.PumlFactory;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramElementCreator;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectCreator;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;


public class BeanFactory {
    // Repository vs Factory vs Creator:
    // - Creator: 从无到有创建, 无逻辑，单纯 new
    // - Factory: 从无到有创建, 有逻辑，无 new （调用 Creator 或其他 Factory）
    // - Repository: 已有的内容的存取（不含 new）

    // Util
    private DirMaker dirMaker;
    private MyConsole myConsole;

    // Creator
    private FileCreator fileCreator;
    private WriterCreator writerCreator;
    private MyDiagramElementCreator myDiagramElementCreator;
    private ViProjectCreator viProjectCreator;
    private MyDiagramCreator myDiagramCreator;
    private PumlDiagramCreator pumlDiagramCreator;
    private TreeVisitorCreator treeVisitorCreator;
    private ExportDiagramVisitorCreator exportDiagramVisitorCreator;

    // Factories
    private ViElementFactory viElementFactory;
    private MyProjectFactory myProjectFactory;
    private MyDiagramElementFactory myDiagramElementFactory;
    private MyPackageFactory myPackageFactory;
    private MyModelFactory myModelFactory;
    private MyClassFactory myClassFactory;
    private MyUseCaseFactory myUseCaseFactory;
    private MyElementFactoryFactory myElementFactoryFactory;
    private MyDiagramFactory myDiagramFactory;
    private PumlFactory pumlFactory;

    // Domain
    private MyElementPool myElementPool;

    // Application Services
    private ExportService exportService;
    private TreeService treeService;
    private ListDiagramsService listDiagramsService;

    // Adapter
    private CLIController cliController;
    private VPProjectRepository vpProjectRepository;
    private DocWriter docWriter;


    public BeanFactory() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {

    }

    private DirMaker getDirMaker() {
        if (dirMaker == null) {
            dirMaker = new DirMaker();
        }
        return dirMaker;
    }

    private VPProjectRepository getVPProjectRepository() {
        if (vpProjectRepository == null) {
            vpProjectRepository = new VPProjectRepository();
        }
        return vpProjectRepository;
    }

    // Factory
    private FileCreator getFileFactory() {
        if (fileCreator == null) {
            fileCreator = new FileCreator();
        }
        return fileCreator;
    }

    private MyDiagramCreator getMyDiagramCreator() {
        if (myDiagramCreator == null) {
            myDiagramCreator = new MyDiagramCreator();
        }
        return myDiagramCreator;
    }

    private WriterCreator getWriterFactory() {
        if (writerCreator == null) {
            writerCreator = new WriterCreator(getFileFactory());
        }
        return writerCreator;
    }

    private MyProjectFactory getMyProjectFactory() {
        if (myProjectFactory == null) {
            myProjectFactory = new MyProjectFactory(getVPProjectRepository());
        }
        return myProjectFactory;
    }

    private MyElementFactoryFactory getMyElementFactoryFactory() {
        if (myElementFactoryFactory == null) {
            myElementFactoryFactory = new MyElementFactoryFactory(
                getMyPackageFactory(),
                getMyClassFactory(),
                getMyModelFactory(),
                getMyUseCaseFactory()
            );
        }
        return myElementFactoryFactory;
    }

    private MyUseCaseFactory getMyUseCaseFactory() {
        if (myUseCaseFactory == null) {
            myUseCaseFactory = new MyUseCaseFactory(getMyElementPool());
        }
        return myUseCaseFactory;
    }

    private MyModelFactory getMyModelFactory() {
        if (myModelFactory == null) {
            myModelFactory = new MyModelFactory(getMyElementPool());
        }
        return myModelFactory;
    }


    private MyClassFactory getMyClassFactory() {
        if (myClassFactory == null) {
            myClassFactory = new MyClassFactory(getMyElementPool());
        }
        return myClassFactory;
    }

    private MyPackageFactory getMyPackageFactory() {
        if (myPackageFactory == null) {
            myPackageFactory = new MyPackageFactory(getMyElementPool());
        }
        return myPackageFactory;
    }

    private MyElementPool getMyElementPool() {
        if (myElementPool == null) {
            myElementPool = new MyElementPool(getMyProjectFactory());
        }
        return myElementPool;
    }

    private ViElementFactory getViElementFactory() {
        if (viElementFactory == null) {
            viElementFactory = new ViElementFactory(getMyElementFactoryFactory());
        }
        return viElementFactory;
    }

    private MyDiagramElementCreator getMyDiagramElementCreator() {
        if (myDiagramElementCreator == null) {
            myDiagramElementCreator = new MyDiagramElementCreator();
        }
        return myDiagramElementCreator;
    }

    private MyDiagramElementFactory getMyDiagramElementFactory() {
        if (myDiagramElementFactory == null) {
            myDiagramElementFactory = new MyDiagramElementFactory(
                getMyElementFactoryFactory(),
                getMyDiagramElementCreator()
            );
        }
        return myDiagramElementFactory;
    }

    private MyDiagramFactory getMyDiagramFactory() {
        if (myDiagramFactory == null) {
            myDiagramFactory = new MyDiagramFactory(
                getMyDiagramCreator(),
                getMyDiagramElementFactory()
            );
        }
        return myDiagramFactory;
    }

    private PumlFactory getToPumlConverter() {
        if (pumlFactory == null) {
            pumlFactory = new PumlFactory(getPumlDiagramCreator());
        }
        return pumlFactory;
    }

    private PumlDiagramCreator getPumlDiagramCreator() {
        if (pumlDiagramCreator == null ) {
            pumlDiagramCreator = new PumlDiagramCreator();
        }
        return pumlDiagramCreator;
    }


    private ViProjectCreator getViProjectFactory() {
        if (viProjectCreator == null) {
            viProjectCreator = new ViProjectCreator(getViElementFactory());
        }
        return viProjectCreator;
    }

    private TreeVisitorCreator getTreeVisitorFactory() {
        if (treeVisitorCreator == null) {
            treeVisitorCreator = new TreeVisitorCreator();
        }
        return treeVisitorCreator;
    }

    private DocWriter getDocWriter() {
        if (docWriter == null) {
            docWriter = new DocWriter();
        }
        return docWriter;
    }

    private ExportDiagramVisitorCreator getExportDiagramVisitorFactory() {
        if (exportDiagramVisitorCreator == null) {
            exportDiagramVisitorCreator = new ExportDiagramVisitorCreator(
                getDirMaker(),
                getMyProjectFactory(),
                getMyDiagramFactory(),
                getToPumlConverter(),
                getDocWriter()
            );
        }
        return exportDiagramVisitorCreator;
    }

    public CLIController getCLIController() {
        if (cliController == null) {
            cliController = new CLIController(
                getExportService(),
                getTreeService(),
                getListDiagramService(),
                getMyConsole());
        }
        return cliController;
    }

    private MyConsole getMyConsole() {
        if (myConsole == null) {
            myConsole = new MyConsole();
        }
        return myConsole;
    }

    private ListDiagramsService getListDiagramService() {
        if (listDiagramsService == null) {
            listDiagramsService = new ListDiagramsService(getVPProjectRepository());
        }
        return listDiagramsService;
    }

    private TreeService getTreeService() {
        if (treeService == null) {
            treeService = new TreeService(
                getVPProjectRepository(),
                getViProjectFactory(),
                getTreeVisitorFactory()
            );
        }
        return treeService;
    }

    private ExportService getExportService() {
        if (exportService == null) {
            exportService = new ExportService(
                getVPProjectRepository(),
                getViProjectFactory(),
                getExportDiagramVisitorFactory(),
                getMyProjectFactory()
            );
        }
        return exportService;
    }
}
