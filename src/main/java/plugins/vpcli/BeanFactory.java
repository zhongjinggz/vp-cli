package plugins.vpcli;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginCommandLineSupport;
import com.vp.plugin.VPPluginInfo;
import org.slf4j.LoggerFactory;
import plugins.vpcli.application.ExportService;
import plugins.vpcli.application.ListDiagramsService;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.writers_old.WriterFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactory;
import plugins.vpcli.domain.myuml.mycommon.MyElementFactoryFactory;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.myuml.myproject.ExportDiagramVisitorFactory;
import plugins.vpcli.domain.myuml.myproject.MyProjectFactory;
import plugins.vpcli.domain.myuml.myproject.TreeElementVisitorFactory;
import plugins.vpcli.domain.outputmodel.MyDiagramToPumlConverter;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.element.MyDiagramElementFactory;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.VPToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.element.ViElementFactory;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.structure.ViProjectFactory;
import plugins.vpcli.drivenadapter.*;
import plugins.vpcli.drivingadapter.CLIController;

import java.io.File;
import java.io.IOException;


public class BeanFactory {

    // Util
    private DirMaker dirMaker;
    private MyConsole myConsole;

    // Factories
    private MyDiagramFactory myDiagramFactory;
    private WriterFactory writerFactory;
    private FileFactory fileFactory;


    //private TreePrinter treePrinter;
    private TreeDirMaker treeDirMaker;

    private ViProjectFactory viProjectFactory;
    private TreeElementVisitorFactory treeVisitorFactory;
    private MyDiagramElementFactory myDiagramElementFactory;

    // Repositories
    private VPProjectRepository vpProjectRepository;


    // Domain Services

    // Application Services
    private ExportService exportService;
    private TreeService treeService;
    private ListDiagramsService listDiagramsService;

    // Driving Adapter
    private CLIController cliController;
    private ExportDiagramVisitorFactory exportVisitorFactory;
    private MyProjectFactory myProjectFactory;
    private ViElementFactory viElementFactory;
    private VPToMyDiagramConverter vpToMyDiagramConverter;
    private MyDiagramToPumlConverter myDiagramToPumlConverter;
    private MyElementFactoryFactory myElementFactoryFactory;


    public BeanFactory() {
        injectDependencies();
    }

    // TODO 优化依赖注入，按功能分开不同的控制器，只注入本次功能需要的依赖
    void injectDependencies() {

        // Repository vs Factory
        // Repository: 已有的内容的存取（不含 new）
        // Factory: 从无到有创建（用 new）

        // Util
        this.dirMaker = new DirMaker();
//        this.myConsole = new MyConsole();

        // Repository
        this.vpProjectRepository = new VPProjectRepository();

        // Factory
        this.fileFactory = new FileFactory();
        this.myDiagramFactory = new MyDiagramFactory();
        this.writerFactory = new WriterFactory(this.fileFactory);
        this.treeDirMaker = new TreeDirMaker();
        this.myProjectFactory = new MyProjectFactory(vpProjectRepository);
        this.myElementFactoryFactory = new MyElementFactoryFactory();
        this.viElementFactory = new ViElementFactory(myElementFactoryFactory);
        this.myDiagramElementFactory = new MyDiagramElementFactory();

        this.vpToMyDiagramConverter = new VPToMyDiagramConverter(myDiagramFactory, myDiagramElementFactory);
        this.myDiagramToPumlConverter = new MyDiagramToPumlConverter();


        this.viProjectFactory = new ViProjectFactory(viElementFactory);
        this.treeVisitorFactory = new TreeElementVisitorFactory();
        this.exportVisitorFactory = new ExportDiagramVisitorFactory(
            this.dirMaker,
            this.myDiagramFactory,
            this.writerFactory,
            this.myProjectFactory,
            this.vpToMyDiagramConverter,
            this.myDiagramToPumlConverter
        );

        // Domain Services

        // Application Services
//        this.exportService = new ExportService(
//            this.vpProjectRepository,
//            this.viProjectFactory,
//            this.exportVisitorFactory,
//            this.myProjectFactory
//        );
//
//        this.treeService = new TreeService(
//            this.vpProjectRepository
//            , this.viProjectFactory
//            , this.treeVisitorFactory);
//
//        this.listDiagramsService = new ListDiagramsService(this.vpProjectRepository);

        // Controller
//        this.cliController = new CLIController(
//            this.exportService
//            , this.treeService
//            , this.listDiagramsService, myConsole);
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
            listDiagramsService = new ListDiagramsService(this.vpProjectRepository);
        }
        return listDiagramsService;
    }

    private TreeService getTreeService() {
        if (treeService == null) {
            treeService = new TreeService(
                this.vpProjectRepository
                , this.viProjectFactory
                , this.treeVisitorFactory);

        }
        return treeService;
    }

    private ExportService getExportService() {
        if (exportService == null) {
            exportService = new ExportService(
                this.vpProjectRepository,
                this.viProjectFactory,
                this.exportVisitorFactory,
                this.myProjectFactory);
        }
        return exportService;
    }

}
