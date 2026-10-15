package plugins.vpcli;

import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.writers_old.WriterCreator;
import plugins.vpcli.domain.myuml.myassociation.MyAssociationFactory;
import plugins.vpcli.domain.myuml.myclassifier.MyClassFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementPool;
import plugins.vpcli.domain.myuml.mycommon.myelementtype.*;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;
import plugins.vpcli.domain.myuml.mymodelrelationshipcontainer.MyModelRelationshipContainerFactory;
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
import plugins.vpcli.util.MyIO;

import static com.vp.plugin.model.factory.IModelElementFactory.*;


public class BeanFactory {
    // Repository vs Factory vs Creator:
    // - Creator: 从无到有创建, 无逻辑，单纯 new
    // - Factory: 从无到有创建, 有逻辑，无 new （调用 Creator 或其他 Factory）
    // - Repository: 已有的内容的存取（不含 new）

    // Util
    private DirMaker dirMaker;
    private MyConsole myConsole;
    private MyIO myIO;

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
    private PumlDiagramFactory pumlDiagramFactory;
    private MyDiagramFactory myDiagramFactory;

    // Abstract Factory Pattern: MyElementFactory
    private MyElementFactoryFactory myElementFactoryFactory;
    private MyPackageFactory myPackageFactory;
    private MyModelFactory myModelFactory;
    private MyClassFactory myClassFactory;
    private MyUseCaseFactory myUseCaseFactory;
    private MyAssociationFactory myAssociationFactory ;
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
    private PumlPackageExporter pumlPackageExporter;
    private PumlClassExporter pumlClassExporter;
    private PumlModelExporter pumlModelExporter;
    private PumlUseCaseExporter pumlUseCaseExporter;
    private PumlAssociationExporter pumlAssociationExporter;
    private PumlModelRelationshipContainerExporter pumlModelRelationshipContainerExporter ;

    // MyElementType
    private MyElementTypes myElementTypes;
    private MyElementTypeProject myElementTypeProject;
    private MyElementTypeClass myElementTypeClass;
    private MyElementTypePackage myElementTypePackage;
    private MyElementTypeModel myElementTypeModel;
    private MyElementTypeUseCase myElementTypeUseCase;
    private MyElementTypeDiagram myElementTypeDiagram;
    private MyElementTypeAssociation myElementTypeAssociation ;
    private MyElementTypeModelRelationshipContainer myElementTypeModelRelationshipContainer;


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
            myProjectFactory = new MyProjectFactory(getVPProjectRepository(), getMyElementTypeProject());
        }
        return myProjectFactory;
    }

    private MyElementFactoryFactory getMyElementFactoryFactory() {
        if (myElementFactoryFactory == null) {
            myElementFactoryFactory = new MyElementFactoryFactory(
                getMyPackageFactory(),
                getMyClassFactory(),
                getMyModelFactory(),
                getMyUseCaseFactory(),
                getMyAssociationFactory(),
                getMyModelRelationshipFactory()
            );
        }
        return myElementFactoryFactory;
    }

    private MyModelRelationshipContainerFactory getMyModelRelationshipFactory() {
        if (myModelRelationshipContainerFactory == null) {
            myModelRelationshipContainerFactory = new MyModelRelationshipContainerFactory(getMyElementPool(), getMyElementTypeModeRelationshipContainer());
        }
        return myModelRelationshipContainerFactory;
    }

    private MyElementTypeModelRelationshipContainer getMyElementTypeModeRelationshipContainer() {
        if (myElementTypeModelRelationshipContainer == null) {
            myElementTypeModelRelationshipContainer = new MyElementTypeModelRelationshipContainer(
                "ModelRelationshipContainer",
                MODEL_TYPE_MODEL_RELATIONSHIP_CONTAINER,
                getPumlModelRelationshipContainerExporter()
            );
            // TODO 在对象内部进行双向关联
            myElementTypeModelRelationshipContainer.setMyElementFactory(getMyModelRelationshipFactory() );
        }
        return myElementTypeModelRelationshipContainer;
    }

    private PumlModelRelationshipContainerExporter getPumlModelRelationshipContainerExporter() {
        if (pumlModelRelationshipContainerExporter == null) {
            pumlModelRelationshipContainerExporter = new PumlModelRelationshipContainerExporter();
        }
        return pumlModelRelationshipContainerExporter;
    }

    private MyAssociationFactory getMyAssociationFactory() {
        if (myAssociationFactory == null ) {
            myAssociationFactory = new MyAssociationFactory(
                getMyElementPool(), getMyElementTypeAssociation());
        }
        return myAssociationFactory;
    }

    private MyElementTypeAssociation getMyElementTypeAssociation() {
        if (myElementTypeAssociation == null) {
            myElementTypeAssociation = new MyElementTypeAssociation(
                "Association",
                MODEL_TYPE_ASSOCIATION,
                getPumlAssociationExporter()
            );
            myElementTypeAssociation.setMyElementFactory(getMyAssociationFactory());
        }
        return myElementTypeAssociation;
    }

    private PumlAssociationExporter getPumlAssociationExporter() {
        if (pumlAssociationExporter == null) {
            pumlAssociationExporter = new PumlAssociationExporter();
        }
        return pumlAssociationExporter;
    }

    private MyUseCaseFactory getMyUseCaseFactory() {
        if (myUseCaseFactory == null) {
            myUseCaseFactory = new MyUseCaseFactory(getMyElementPool(), getMyElementTypeUseCase());
        }
        return myUseCaseFactory;
    }

    private MyModelFactory getMyModelFactory() {
        if (myModelFactory == null) {
            myModelFactory = new MyModelFactory(getMyElementPool(), getMyElementTypeModel());
        }
        return myModelFactory;
    }


    private MyClassFactory getMyClassFactory() {
        if (myClassFactory == null) {
            myClassFactory = new MyClassFactory(getMyElementPool(), getMyElementTypeClass());
        }
        return myClassFactory;
    }

    private MyPackageFactory getMyPackageFactory() {
        if (myPackageFactory == null) {
            myPackageFactory = new MyPackageFactory(getMyElementPool(), getMyElementTypePackage());
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

    private PumlDiagramFactory getPumlFactory() {
        if (pumlDiagramFactory == null) {
            pumlDiagramFactory = new PumlDiagramFactory(getPumlDiagramCreator());
        }
        return pumlDiagramFactory;
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

    private ExportDiagramVisitorCreator getExportDiagramVisitorFactory() {
        if (exportDiagramVisitorCreator == null) {
            exportDiagramVisitorCreator = new ExportDiagramVisitorCreator(
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
                getMyElementTypeDiagram(),
                getMyElementTypeAssociation()

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
                getPumlClassExporter()
            );
            myElementTypeClass.setMyElementFactory(getMyClassFactory());
        }
        return myElementTypeClass;
    }

    private MyElementTypePackage getMyElementTypePackage() {
        if (myElementTypePackage == null) {
            myElementTypePackage = new MyElementTypePackage(
                "Package",
                MODEL_TYPE_PACKAGE,
                getPumlPackageExporter()
            );
            myElementTypePackage.setMyElementFactory(getMyPackageFactory());
        }
        return myElementTypePackage;
    }


    private MyElementTypeModel getMyElementTypeModel() {
        if (myElementTypeModel == null) {
            myElementTypeModel = new MyElementTypeModel(
                "Model",
                MODEL_TYPE_MODEL,
                getPumlModelExporter()
            );
            myElementTypeModel.setMyElementFactory(getMyModelFactory());
        }
        return myElementTypeModel;
    }

    private MyElementTypeUseCase getMyElementTypeUseCase() {
        if (myElementTypeUseCase == null) {
            myElementTypeUseCase = new MyElementTypeUseCase(
                "UseCase",
                MODEL_TYPE_USE_CASE,
                getPumlUseCaseExporter()
            );
            myElementTypeUseCase.setMyElementFactory(getMyUseCaseFactory());
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
