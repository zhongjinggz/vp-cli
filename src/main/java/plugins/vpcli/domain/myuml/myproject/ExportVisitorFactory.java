package plugins.vpcli.domain.myuml.myproject;

import plugins.vpcli.domain.vpstruct.VPVisitor;
import plugins.vpcli.domain.vpstruct.VPVisitorFactory;
import plugins.vpcli.drivenadapter.FileIO;

import java.io.File;

public class ExportVisitorFactory implements VPVisitorFactory {

    private final FileIO fileIO;
    private File rootDir;

    public ExportVisitorFactory(FileIO fileIO) {
        this.fileIO = fileIO;

    }

    @Override
    public VPVisitor create(VPVisitor preLevelVisitor) {
        ExportVisitor result;
        if (preLevelVisitor != null) {
            result = new ExportVisitor((ExportVisitor) preLevelVisitor, this.fileIO);
        } else {
            result = new ExportVisitor(null, this.fileIO);
            result.setParentDir(rootDir);
        }
        return result;

    }

    public void setRootDir(File dir) {
        this.rootDir = dir;
    }
}
