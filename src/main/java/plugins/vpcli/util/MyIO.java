package plugins.vpcli.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyIO {
    public void writeFile(File parentDir, String fileName, String pumlText) throws IOException {
        File outputFile = new File(parentDir, fileName);
        Path path = outputFile.toPath();
        Files.writeString(path, pumlText, StandardCharsets.UTF_8);
    }
}
