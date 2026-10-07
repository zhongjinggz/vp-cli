package plugins.vpcli.domain.myuml.myproject;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.vpcli.application.writers.ClassUMLWriter;
import plugins.vpcli.application.writers.WriterFactory;
import plugins.vpcli.domain.mydiagram.MyClassDiagram;
import plugins.vpcli.domain.mydiagram.MyDiagramFactory;
import plugins.vpcli.domain.vpstruct.VPDiagram;
import plugins.vpcli.domain.vpstruct.VPStructElement;
import plugins.vpcli.domain.vpstruct.VPVisitor;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;
import java.io.IOException;

import static plugins.vpcli.domain.vpstruct.ElementType.*;

public class ExportVisitor extends VPVisitor {
    private final FileIO fileIO;
    private final MyDiagramFactory diagramFactory;
    private final WriterFactory writerFactory;
    private final FileFactory fileFactory;
    private File path;
    private File parentDir;

    public ExportVisitor(
        ExportVisitor preLevelVisitor
        , FileIO fileIO
        , MyDiagramFactory diagramFactory
        , WriterFactory writerFactory
        , FileFactory fileFactory) {

        this.fileIO = fileIO;
        this.diagramFactory = diagramFactory;
        this.writerFactory = writerFactory;
        this.fileFactory = fileFactory;
        if (preLevelVisitor != null) {
            this.parentDir = preLevelVisitor.getPath();
        }
    }

    public void setParentDir(File dir) {
        this.parentDir = dir;

    }

    @Override
    public void visit(VPStructElement element) throws IOException {
        var suffix = element.getType().getSuffix();
        if (element.typeIs(PACKAGE) || element.typeIs(MODEL)) {
            this.path = fileIO.makeDir
                .named(element.getName() + suffix)
                .under(parentDir);
        } else if (element.typeIs(CLASS_DIAGRAM)) {
            var vpDiagram = ((VPDiagram)element).getVPDiagram();
            exportClassDiagram(vpDiagram, parentDir);

        }
    }

    File getPath() {
        return path;
    }

    private void exportClassDiagram(IDiagramUIModel vpDiagram
        , File parentDir) throws IOException {
        File outputFile = createOutputFile(vpDiagram.getName(), parentDir);
        MyClassDiagram exporter = this.diagramFactory.createClassDiagramExporter(vpDiagram);
        exporter.extract();
        ClassUMLWriter classWriter = writerFactory.createClassUMLWriter(exporter);
        classWriter.writeToFile(outputFile);
    }

    File createOutputFile(String title, File exportLocation) throws IOException {
        StringBuilder fileName = new StringBuilder();
        // 放行所有语言的字母和数字（中文文件名）；空格、符号及 Windows 保留字符仍转下划线
        fileName.append(title.replaceAll("[^\\p{L}\\p{N}]", "_"));
        fileName.append(".puml");
        File outputFile = fileFactory.createFile(exportLocation, fileName.toString());
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
        }
        return outputFile;
    }
}


