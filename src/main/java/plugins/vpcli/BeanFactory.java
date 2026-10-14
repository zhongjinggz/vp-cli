package plugins.vpcli;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.writers_old.WriterCreator;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.*;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;
import plugins.vpcli.domain.myuml.mypackage.MyModelFactory;
import plugins.vpcli.domain.myuml.mypackage.MyPackageFactory;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorCreator;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeVisitorCreator;
import plugins.vpcli.domain.myuml.myusecase.MyUseCaseFactory;
import plugins.vpcli.domain.outputmodel.*;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramElementCreator;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.mydiagramfactory.MyDiagramFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectCreator;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;

import static com.vp.plugin.model.factory.IModelElementFactory.*;


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
    private PumlFactory pumlFactory;
    private MyDiagramFactory myDiagramFactory;

    // Abstract Factory Pattern: MyElementFactory
    private MyElementFactoryFactory myElementFactoryFactory;
    private MyPackageFactory myPackageFactory;
    private MyModelFactory myModelFactory;
    private MyClassFactory myClassFactory;
    private MyUseCaseFactory myUseCaseFactory;


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
    private PumlPackageExporter pumlPackageExporter;
    private PumlClassExporter pumlClassExporter;
    private PumlModelExporter pumlModelExporter;
    private PumlUseCaseExporter pumlUseCaseExporter;

    // MyElementType
    private MyElementTypes myElementTypes;
    private MyElementTypeProject myElementTypeProject;
    private MyElementTypeClass myElementTypeClass;
    private MyElementTypePackage myElementTypePackage;
    private MyElementTypeModel myElementTypeModel;
    private MyElementTypeUseCase myElementTypeUseCase;
    private MyElementTypeDiagram myElementTypeDiagram;


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
            myProjectFactory = new MyProjectFactory(getVPProjectRepository(), getMyElementTypes());
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
            myUseCaseFactory = new MyUseCaseFactory(getMyElementPool(), getMyElementTypes());
        }
        return myUseCaseFactory;
    }

    private MyModelFactory getMyModelFactory() {
        if (myModelFactory == null) {
            myModelFactory = new MyModelFactory(getMyElementPool(), getMyElementTypes());
        }
        return myModelFactory;
    }


    private MyClassFactory getMyClassFactory() {
        if (myClassFactory == null) {
            myClassFactory = new MyClassFactory(getMyElementPool(), getMyElementTypes());
        }
        return myClassFactory;
    }

    private MyPackageFactory getMyPackageFactory() {
        if (myPackageFactory == null) {
            myPackageFactory = new MyPackageFactory(getMyElementPool(), getMyElementTypes());
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
                getMyDiagramElementFactory(),
                getMyElementTypes()
            );
        }
        return myDiagramFactory;
    }

    private PumlFactory getToPumlConverter() {
        if (pumlFactory == null) {
//            pumlFactory = new PumlFactory(getPumlDiagramCreator());
        }
        return pumlFactory;
    }

    private PumlDiagramCreator getPumlDiagramCreator() {
        if (pumlDiagramCreator == null) {
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

    // PumlElementExporter
    private PumlPackageExporter getPumlPackageExporter() {
        if (pumlPackageExporter == null) {
            pumlPackageExporter = new PumlPackageExporter();
        }
        return pumlPackageExporter;
    }

    private PumlClassExporter getPumlClassExporter() {
        if (pumlClassExporter == null) {
            pumlClassExporter = new PumlClassExporter();
        }
        return pumlClassExporter;
    }

    private PumlModelExporter getPumlModelExporter() {
        if (pumlModelExporter == null) {
            pumlModelExporter = new PumlModelExporter();
        }
        return pumlModelExporter;
    }

    private PumlUseCaseExporter getPumlUseCaseExporter() {
        if (pumlUseCaseExporter == null) {
            pumlUseCaseExporter = new PumlUseCaseExporter();
        }
        return pumlUseCaseExporter;
    }

    // MyElementType
    private MyElementTypes getMyElementTypes() {
        if (myElementTypes == null) {
            myElementTypes = new MyElementTypes(
                getMyElementTypeProject(),
                getMyElementTypeClass(),
                getMyElementTypePackage(),
                getMyElementTypeModel(),
                getMyElementTypeUseCase(),
                getMyElementTypeDiagram()
            );
        }
        return myElementTypes;
    }

    private MyElementTypeProject getMyElementTypeProject() {
        if (myElementTypeProject == null) {
            myElementTypeProject = new MyElementTypeProject(
                "Project",
                "project_has_no_visual_paradigm_model_type",
                null,
                null
            );
        }
        return myElementTypeProject;
    }

    private MyElementTypeClass getMyElementTypeClass() {
        if (myElementTypeClass == null) {
            myElementTypeClass = new MyElementTypeClass(
                "Class",
                IModelElementFactory.MODEL_TYPE_CLASS,
                getPumlClassExporter(),
                getMyClassFactory()
            );
        }
        return myElementTypeClass;
    }

    private MyElementTypePackage getMyElementTypePackage() {
        if (myElementTypePackage == null) {
            myElementTypePackage = new MyElementTypePackage(
                "Package",
                MODEL_TYPE_PACKAGE,
                getPumlPackageExporter(),
                getMyPackageFactory()
            );
        }
        return myElementTypePackage;
    }


    private MyElementTypeModel getMyElementTypeModel() {
        if (myElementTypeModel == null) {
            myElementTypeModel = new MyElementTypeModel(
                "Model",
                MODEL_TYPE_MODEL,
                getPumlModelExporter(),
                getMyModelFactory()
            );
        }
        return myElementTypeModel;
    }

    private MyElementTypeUseCase getMyElementTypeUseCase() {
        if (myElementTypeUseCase == null) {
            myElementTypeUseCase = new MyElementTypeUseCase(
                "UseCase",
                MODEL_TYPE_USE_CASE,
                getPumlUseCaseExporter(),
                getMyUseCaseFactory()
            );
        }
        return myElementTypeUseCase;
    }

    private MyElementTypeDiagram getMyElementTypeDiagram() {
        if (myElementTypeDiagram == null) {
            myElementTypeDiagram = new MyElementTypeDiagram(
                "Diagram",
                "diagram_has_no_visual_paradigm_model_type",
                null,
                null
            );
        }
        return myElementTypeDiagram;
    }


}
