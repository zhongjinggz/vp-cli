package plugins.plantUML;

import java.io.File;

public class FileFactory {
    public File createFile(File parent, String child) {
        return new File(parent, child);
    }
}
