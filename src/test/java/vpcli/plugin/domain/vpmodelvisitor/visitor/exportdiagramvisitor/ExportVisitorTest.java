package vpcli.plugin.domain.vpmodelvisitor.visitor.exportdiagramvisitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagram;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagramFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.ViDiagramAsElement;
import vpcli.plugin.domain.vpmodelvisitor.element.ViElementFactory;
import vpcli.plugin.domain.vpmodelvisitor.element.ViPackage;
import vpcli.plugin.drivenadapter.DocWriter;
import vpcli.plugin.util.MyIO;

/**
 * 验证 ExportDiagramVisitor 以 preLevelPath 代替 higherVisitor.getPath()：
 * 子节点的写入目录来自父节点路径（由 Factory 的 build 显式注入），
 * root 目录通过 setRootDir 冻结到工厂、由 buildRoot() 出货。
 */
class ExportVisitorTest {

    @TempDir
    Path tempDir;

    private final MyIO myIO = new MyIO();
    private final DocWriter docWriter = mock(DocWriter.class);
    private final PumlDiagramFactory pumlDiagramFactory = mock(PumlDiagramFactory.class);
    @SuppressWarnings("unused")
    private final ViElementFactory viElementFactory = new ViElementFactory();

    private ExportVisitorFactory newFactory(File rootDir) {
        ExportVisitorFactory factory = new ExportVisitorFactory(
            pumlDiagramFactory, docWriter, myIO);
        factory.setRootDir(rootDir);
        return factory;
    }

    @Test
    void buildRoot_usesFrozenRootDirAsPath() {
        File rootDir = tempDir.toFile();
        RootExportVisitor root =
            (RootExportVisitor) newFactory(rootDir).buildRoot();

        assertEquals(rootDir, root.getPath());
    }

    @Test
    void visitDiagram_writesToParentPreLevelPath() throws IOException {
        File rootDir = tempDir.toFile();
        ExportVisitorFactory factory = newFactory(rootDir);

        RootExportVisitor root = (RootExportVisitor) factory.buildRoot();
        PracticalExportVisitor child =
            (PracticalExportVisitor) factory.build(root, false);

        // 单层：父就是 root，root 的 path 即 rootDir；子 diagram 应写入 rootDir。
        PumlDiagram puml = new PumlDiagram("D");
        when(pumlDiagramFactory.build(any(IDiagramUIModel.class))).thenReturn(puml);

        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getName()).thenReturn("D");
        child.visit(new ViDiagramAsElement(diagram));

        verify(docWriter).plantUML(puml, rootDir);
    }

    @Test
    void visitDiagram_usesParentPathAsTargetDir() {
        // 两层：父是一个 Package 目录，子 diagram 应写入该目录而非 root。
        File rootDir = tempDir.toFile();
        ExportVisitorFactory factory = newFactory(rootDir);

        RootExportVisitor root = (RootExportVisitor) factory.buildRoot();
        PracticalExportVisitor pkg = (PracticalExportVisitor) factory.build(root, false);

        IModelElement pkgElement = mock(IModelElement.class);
        when(pkgElement.getName()).thenReturn("pkg");
        when(pkgElement.getModelType())
            .thenReturn(com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_PACKAGE);
        pkg.visit(new ViPackage(pkgElement, viElementFactory));

        // makeDirOf 已用 preLevelPath(rootDir) 构造出父目录。
        File pkgDir = pkg.getPath();
        assertEquals(new File(rootDir, "pkg.package"), pkgDir);

        PracticalExportVisitor child = (PracticalExportVisitor) factory.build(pkg, false);

        PumlDiagram puml = new PumlDiagram("D");
        when(pumlDiagramFactory.build(any(IDiagramUIModel.class))).thenReturn(puml);

        IDiagramUIModel diagram = mock(IDiagramUIModel.class);
        when(diagram.getName()).thenReturn("D");
        child.visit(new ViDiagramAsElement(diagram));

        verify(docWriter).plantUML(eq(puml), eq(pkgDir));
    }
}
