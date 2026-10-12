package plugins.vpcli.domain.myuml.myproject;

import org.jetbrains.annotations.NotNull;
import plugins.vpcli.application.writers_old.WriterCreator;
import plugins.vpcli.domain.myuml.mydiagram.MyDiagramCreator;
import plugins.vpcli.domain.outputmodel.ToPumlConverter;
import plugins.vpcli.domain.vpvisitor.vpdiagramvisitor.structure.ToMyDiagramConverter;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitor;
import plugins.vpcli.domain.vpvisitor.vpmodelvisitor.visitor.ViElementVisitorFactory;
import plugins.vpcli.drivenadapter.DirMaker;

import java.io.File;

public class ExportDiagramVisitorCreator implements ViElementVisitorFactory {

    private final MyDiagramCreator diagramFactory;
    private final WriterCreator writerCreator;
    private final MyProjectFactory myProjectFactory;
    private final DirMaker dirMaker;
    private final ToMyDiagramConverter toMyDiagramConverter;
    private final ToPumlConverter toPumlConverter;

    private File rootDir;

    public ExportDiagramVisitorCreator(
        DirMaker dirMaker,
        MyDiagramCreator diagramFactory,
        WriterCreator writerCreator,
        MyProjectFactory myProjectFactory,
        ToMyDiagramConverter toMyDiagramConverter,
        ToPumlConverter toPumlConverter
    ) {
        this.dirMaker = dirMaker;
        this.diagramFactory = diagramFactory;
        this.writerCreator = writerCreator;
        this.myProjectFactory = myProjectFactory;
        this.toMyDiagramConverter = toMyDiagramConverter;
        this.toPumlConverter = toPumlConverter;
    }

    @Override
    public ViElementVisitor under(@NotNull ViElementVisitor higherVisitor) {

        var myProject = myProjectFactory.fromGlobal();

        return new ExportDiagramVisitor(
            (ExportDiagramVisitor) higherVisitor,
            this.dirMaker,
            diagramFactory,
            writerCreator,
            myProject,
            toMyDiagramConverter,
            toPumlConverter
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
