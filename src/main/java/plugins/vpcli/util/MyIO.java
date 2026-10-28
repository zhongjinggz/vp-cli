package plugins.vpcli.util;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.LoggerFactory;

@Singleton
public class MyIO {
    private static final Logger logger = LoggerFactory.getLogger(MyIO.class);

    @Inject
    public MyIO() {
    }

    public void writeFile(File parentDir, String fileName, String text) throws IOException {
        File outputFile = new File(parentDir, fileName);
        Path path = outputFile.toPath();
        logger.debug("Writing file {} to {} ", fileName, path);
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public File makeDir(File parentDir, String dirName) {
        var path = new File(parentDir, dirName);
        if (!path.exists()) {
            path.mkdirs();
        }
        logger.debug("Making directory {} ", path.getAbsolutePath());
        return path;
    }
}
