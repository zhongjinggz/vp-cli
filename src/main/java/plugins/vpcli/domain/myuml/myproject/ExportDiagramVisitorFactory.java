package plugins.vpcli.domain.myuml.myproject;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.application.writers_old.WriterFactory;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.myuml.mydiagram_old.OldDiagramFactory;
import plugins.vpcli.domain.outputmodel.MyDiagramToPumlConverter;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.VPToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.DirMaker;

import java.io.File;

public class ExportDiagramVisitorFactory implements ViElementVisitorFactory {

    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private final MyProjectFactory myProjectFactory;
    private final DirMaker dirMaker;
    private final VPToMyDiagramConverter vpToMyDiagramConverter;
    private final MyDiagramToPumlConverter myDiagramToPumlConverter;

    private File rootDir;

    public ExportDiagramVisitorFactory(
        DirMaker dirMaker,
        MyDiagramFactory diagramFactory,
        WriterFactory writerFactory,
        MyProjectFactory myProjectFactory,
        VPToMyDiagramConverter vpToMyDiagramConverter,
        MyDiagramToPumlConverter myDiagramToPumlConverter
    ) {
        this.dirMaker = dirMaker;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.myProjectFactory = myProjectFactory;
        this.vpToMyDiagramConverter = vpToMyDiagramConverter;
        this.myDiagramToPumlConverter = myDiagramToPumlConverter;
    }

    @Override
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {

        var myProject = myProjectFactory.fromGlobal();

        return new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            this.dirMaker,
            diagramFactory,
            writerFactory,
            myProject,
            vpToMyDiagramConverter,
            myDiagramToPumlConverter
        );

    }

    @Override
    public ViElementVisitor createRoot() {
        var myProject = myProjectFactory.fromGlobal();
        return new RootExportDiagramVisitor(this.rootDir, myProject);
    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
