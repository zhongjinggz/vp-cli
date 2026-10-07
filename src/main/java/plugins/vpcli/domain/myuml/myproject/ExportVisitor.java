package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpstruct.VPStructElement;
import plugins.vpcli.domain.vpstruct.VPVisitor;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;

import static plugins.vpcli.domain.vpstruct.ElementType.*;

public class ExportVisitor extends VPVisitor {
    private final FileIO fileIO;
    private File path;
    private final ExportVisitor preLevelVisitor;
    private File parentDir;

    public ExportVisitor(ExportVisitor preLevelVisitor, FileIO fileIO) {
        this.preLevelVisitor = preLevelVisitor;
        this.fileIO = fileIO;
        if (preLevelVisitor != null) {
            this.parentDir = preLevelVisitor.getPath();
        }
    }

    public void setParentDir(File dir) {
        this.parentDir = dir;

    }

    @Override
    public void visit(VPStructElement element) {
        var suffix = element.getType().getSuffix();
        if (element.typeIs(PACKAGE) || element.typeIs(MODEL)) {
            this.path = fileIO.makeDir
                .named(element.getName() + suffix)
                .under(parentDir);
        }
    }

    File getPath() {
        return path;
    }
}


