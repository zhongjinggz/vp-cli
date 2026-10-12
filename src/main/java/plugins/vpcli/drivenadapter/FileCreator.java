package plugins.vpcli.drivenadapter;

import java.io.File;

public class FileCreator {
    public File under(File parent, String child) {
        return new File(parent, child);
    }
}
