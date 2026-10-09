package plugins.vpcli.drivenadapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;

public class DirMaker {
    private String dirName;

    public DirMaker named(@NonNull String dirName) {
        this.dirName = dirName;
        return this;
    }

    public File under(File parentDir) {
        var path = new File(parentDir, dirName);
        if (!path.exists()) {
            path.mkdirs();
        }
        return path;
    }

}
