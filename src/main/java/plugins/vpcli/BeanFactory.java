package plugins.vpcli;

import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorFactory;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;
import plugins.vpcli.domain.outputmodel.*;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.util.MyIO;


public class BeanFactory {
    // Repository vs Factory
    // - Factory: 从无到有创建，一般有 new，或调用其他 Factory
    // - Repository: 已有的内容的存取（不含 new）

    // Util
    private DirMaker dirMaker;
    private MyConsole myConsole;
    private MyIO myIO;


    // Factories
    private ViProjectFactory viProjectFactory;
    private ViElementFactory viElementFactory;

    private TreeVisitorFactory treeVisitorCreator;
    private ExportDiagramVisitorFactory exportDiagramVisitorCreator;

    private MyProjectFactory myProjectFactory;
    private MyDiagramFactory myDiagramFactory;
    private MyDiagramElementFactory myDiagramElementFactory;

    private PumlDiagramFactory pumlDiagramFactory;

    // Abstract Factory Pattern: MyElementFactory
    private MyElementFactoryFactory myElementFactoryFactory;
    private MyPackageFactory myPackageFactory;
    private MyModelFactory myModelFactory;
    private MyClassFactory myClassFactory;
    private MyUseCaseFactory myUseCaseFactory;
    private MyAssociationFactory myAssociationFactory;
    private MyModelRelationshipContainerFactory myModelRelationshipContainerFactory;


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

    // PumlElementExporter
    private PumlElementExporterFactory pumlElementExporterFactory;
    private PumlPackageExporter pumlPackageExporter;
    private PumlClassExporter pumlClassExporter;
    private PumlModelExporter pumlModelExporter;
    private PumlUseCaseExporter pumlUseCaseExporter;
    private PumlAssociationExporter pumlAssociationExporter;
    private PumlModelRelationshipContainerExporter pumlModelRelationshipContainerExporter;


    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    public BeanFactory() {
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

    private MyProjectFactory getMyProjectFactory() {
        if (myProjectFactory == null) {
            myProjectFactory = new MyProjectFactory(
                getVPProjectRepository()
            );
        }
        return myProjectFactory;
    }

    private MyElementFactoryFactory getMyElementFactoryFactory() {
        if (myElementFactoryFactory == null) {
            myElementFactoryFactory = new MyElementFactoryFactory(
//                getMyProjectFactory()   // this will introduce dead loop
//                , getMyDiagramFactory() // this will introduce dead loop
                getMyPackageFactory()
                , getMyClassFactory()
                , getMyModelFactory()
                , getMyUseCaseFactory()
                , getMyAssociationFactory()
                , getMyModelRelationshipFactory()
            );
        }
        return myElementFactoryFactory;
    }

    private MyModelRelationshipContainerFactory getMyModelRelationshipFactory() {
        if (myModelRelationshipContainerFactory == null) {
            myModelRelationshipContainerFactory = new MyModelRelationshipContainerFactory(
                getMyElementPool()
//                , getMyElementTypeModeRelationshipContainer()
//                , getMyElementTypeRepository()
            );
        }
        return myModelRelationshipContainerFactory;
    }

    private PumlModelRelationshipContainerExporter getPumlModelRelationshipContainerExporter() {
        if (pumlModelRelationshipContainerExporter == null) {
            pumlModelRelationshipContainerExporter = new PumlModelRelationshipContainerExporter(
                // getMyElementTypeRepository()
//                , getPumlElementExporterFactory()
            );
        }
        return pumlModelRelationshipContainerExporter;
    }

    private MyAssociationFactory getMyAssociationFactory() {
        if (myAssociationFactory == null) {
            myAssociationFactory = new MyAssociationFactory(
                getMyElementPool()
//                , getMyElementTypeAssociation()
//                , getMyElementTypeRepository()
            );
        }
        return myAssociationFactory;
    }

    private PumlAssociationExporter getPumlAssociationExporter() {
        if (pumlAssociationExporter == null) {
            pumlAssociationExporter = new PumlAssociationExporter(
//                getMyElementTypeRepository()
//                , getPumlElementExporterFactory()
            );
        }
        return pumlAssociationExporter;
    }

    private MyUseCaseFactory getMyUseCaseFactory() {
        if (myUseCaseFactory == null) {
            myUseCaseFactory = new MyUseCaseFactory(getMyElementPool()
//                , getMyElementTypeUseCase()
//                , getMyElementTypeRepository()
            );
        }
        return myUseCaseFactory;
    }

    private MyModelFactory getMyModelFactory() {
        if (myModelFactory == null) {
            myModelFactory = new MyModelFactory(
                getMyElementPool()
//                , getMyElementTypeModel()
//                , getMyElementTypeRepository()
            );
        }
        return myModelFactory;
    }


    private MyClassFactory getMyClassFactory() {
        if (myClassFactory == null) {
            myClassFactory = new MyClassFactory(
                getMyElementPool()
//                , getMyElementTypeClass()
//                , getMyElementTypeRepository()
            );
        }
        return myClassFactory;
    }

    private MyPackageFactory getMyPackageFactory() {
        if (myPackageFactory == null) {
            myPackageFactory = new MyPackageFactory(getMyElementPool()
//                , getMyElementTypePackage()
//                , getMyElementTypeRepository()
                );
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
            myDiagramElementFactory = new MyDiagramElementFactory(
                getMyElementFactoryFactory()
//                , getMyDiagramElementCreator()
            );
        }
        return myDiagramElementFactory;
    }

    private MyDiagramFactory getMyDiagramFactory() {
        if (myDiagramFactory == null) {
            myDiagramFactory = new MyDiagramFactory(
                getMyDiagramElementFactory()
            );
        }
        return myDiagramFactory;
    }

    private PumlDiagramFactory getPumlFactory() {
        if (pumlDiagramFactory == null) {
            pumlDiagramFactory = new PumlDiagramFactory(
                getPumlElementExporterFactory());
        }
        return pumlDiagramFactory;
    }


    private ViProjectFactory getViProjectFactory() {
        if (viProjectFactory == null) {
            viProjectFactory = new ViProjectFactory(getViElementFactory());
        }
        return viProjectFactory;
    }

    private TreeVisitorFactory getTreeVisitorFactory() {
        if (treeVisitorCreator == null) {
            treeVisitorCreator = new TreeVisitorFactory();
        }
        return treeVisitorCreator;
    }

    private DocWriter getDocWriter() {
        if (docWriter == null) {
            docWriter = new DocWriter(getMyIO());
        }
        return docWriter;
    }

    private MyIO getMyIO() {
        if (myIO == null) {
            myIO = new MyIO();
        }
        return myIO;
    }

    private ExportDiagramVisitorFactory getExportDiagramVisitorFactory() {
        if (exportDiagramVisitorCreator == null) {
            exportDiagramVisitorCreator = new ExportDiagramVisitorFactory(
                getDirMaker(),
                getMyProjectFactory(),
                getMyDiagramFactory(),
                getPumlFactory(),
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
                getExportDiagramVisitorFactory()
//                , getMyProjectFactory()
            );
        }
        return exportService;
    }

    // PumlElementExporter
    private PumlPackageExporter getPumlPackageExporter() {
        if (pumlPackageExporter == null) {
            pumlPackageExporter = new PumlPackageExporter(
                // getMyElementTypeRepository()
//                , getPumlElementExporterFactory()
            );
        }
        return pumlPackageExporter;
    }

    private PumlClassExporter getPumlClassExporter() {
        if (pumlClassExporter == null) {
            pumlClassExporter = new PumlClassExporter(
//                getMyElementTypeRepository()
//                , getPumlElementExporterFactory()
            );
        }
        return pumlClassExporter;
    }

    private PumlModelExporter getPumlModelExporter() {
        if (pumlModelExporter == null) {
            pumlModelExporter = new PumlModelExporter(
                //getMyElementTypeRepository()
//                , getPumlElementExporterFactory()
            );
        }
        return pumlModelExporter;
    }

    private PumlUseCaseExporter getPumlUseCaseExporter() {
        if (pumlUseCaseExporter == null) {
            pumlUseCaseExporter = new PumlUseCaseExporter(
                // getMyElementTypeRepository()
//                , getPumlElementExporterFactory()
            );
        }
        return pumlUseCaseExporter;
    }


    private PumlElementExporterFactory getPumlElementExporterFactory() {
        if (pumlElementExporterFactory == null) {
            pumlElementExporterFactory = new PumlElementExporterFactory(
                getPumlAssociationExporter(),
                getPumlClassExporter(),
                getPumlPackageExporter(),
                getPumlModelExporter(),
                getPumlModelRelationshipContainerExporter(),
                getPumlUseCaseExporter()
            );
        }
        return pumlElementExporterFactory;
    }


}
