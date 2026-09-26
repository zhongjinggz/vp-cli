package plugins.vpcli.drivenadapter;

import java.io.File;

public class FileFactory {
    public File createFile(File parent, String child) {
        return new File(parent, child);
    }
}
