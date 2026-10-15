package plugins.vpcli.drivenadapter;

import plugins.vpcli.domain.outputmodel.PumlDiagram;
import plugins.vpcli.util.MyIO;

import java.io.File;
import java.io.IOException;

public class DocWriter {
    private MyIO io;

    public DocWriter(MyIO io) {
        this.io = io;
    }

    void markdown() {

    }

    public void plantUML(PumlDiagram puml, File parentDir) throws IOException {
        String fileName = puml.getName() + ".puml";
        String pumlText = puml.toString();

        io.writeFile(parentDir, fileName, pumlText);
    }
}
