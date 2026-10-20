package plugins.vpcli.drivenadapter;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DirMaker {
    private String dirName;

    public DirMaker named(@NotNull String dirName) {
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
