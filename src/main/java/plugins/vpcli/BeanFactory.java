package plugins.vpcli;

import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.writers_old.WriterFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;
import plugins.vpcli.domain.outputmodel.ToPumlConverter;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.MyDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.ToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;


public class BeanFactory {
    // Repository vs Factory
    // Repository: 已有的内容的存取（不含 new）
    // Factory: 从无到有创建（用 new）

    // Util
    private DirMaker dirMaker;
    private MyConsole myConsole;

    // Factories
    private FileFactory fileFactory;
    private WriterFactory writerFactory;

    private ViProjectFactory viProjectFactory;
    private ViElementFactory viElementFactory;

    private MyProjectFactory myProjectFactory;
    private MyDiagramFactory myDiagramFactory;
    private MyDiagramElementFactory myDiagramElementFactory;

    private MyElementPool myElementPool;
    private MyPackageFactory myPackageFactory;
    private MyModelFactory myModelFactory;
    private MyClassFactory myClassFactory;
    private MyUseCaseFactory myUseCaseFactory;

    private MyElementFactoryFactory myElementFactoryFactory;

    private TreeVisitorFactory treeVisitorFactory;
    private ExportDiagramVisitorFactory exportDiagramVisitorFactory;

    // Repositories
    private VPProjectRepository vpProjectRepository;

    // Application Services
    private ExportService exportService;
    private TreeService treeService;
    private ListDiagramsService listDiagramsService;

    // Controller
    private CLIController cliController;


    // Domain Service
    private ToMyDiagramConverter toMyDiagramConverter;
    private ToPumlConverter toPumlConverter;


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
    private FileFactory getFileFactory() {
        if (fileFactory == null) {
            fileFactory = new FileFactory();
        }
        return fileFactory;
    }

    private MyDiagramFactory getMyDiagramFactory() {
        if (myDiagramFactory == null) {
            myDiagramFactory = new MyDiagramFactory();
        }
        return myDiagramFactory;
    }

    private WriterFactory getWriterFactory() {
        if (writerFactory == null) {
            writerFactory = new WriterFactory(getFileFactory());
        }
        return writerFactory;
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


    private MyDiagramElementFactory getMyDiagramElementFactory() {
        if (myDiagramElementFactory == null) {
            myDiagramElementFactory = new MyDiagramElementFactory();
        }
        return myDiagramElementFactory;
    }

    private ToMyDiagramConverter getToMyDiagramConverter() {
        if (toMyDiagramConverter == null) {
            toMyDiagramConverter = new ToMyDiagramConverter(
                getMyDiagramFactory(),
                getMyDiagramElementFactory()
            );
        }
        return toMyDiagramConverter;
    }

    private ToPumlConverter getToPumlConverter() {
        if (toPumlConverter == null) {
            toPumlConverter = new ToPumlConverter();
        }
        return toPumlConverter;
    }


    private ViProjectFactory getViProjectFactory() {
        if (viProjectFactory == null) {
            viProjectFactory = new ViProjectFactory(getViElementFactory());
        }
        return viProjectFactory;
    }

    private TreeVisitorFactory getTreeVisitorFactory() {
        if (treeVisitorFactory == null) {
            treeVisitorFactory = new TreeVisitorFactory();
        }
        return treeVisitorFactory;
    }

    private ExportDiagramVisitorFactory getExportDiagramVisitorFactory() {
        if (exportDiagramVisitorFactory == null) {
            exportDiagramVisitorFactory = new ExportDiagramVisitorFactory(
                getDirMaker(),
                getMyDiagramFactory(),
                getWriterFactory(),
                getMyProjectFactory(),
                getToMyDiagramConverter(),
                getToPumlConverter()
            );
        }
        return exportDiagramVisitorFactory;
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
