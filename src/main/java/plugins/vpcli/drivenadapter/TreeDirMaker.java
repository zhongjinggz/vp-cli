package plugins.vpcli.drivenadapter;

import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.io.File;
import java.util.List;

public class TreeDirMaker {
    public void forPackages(List<MyPackage> packages, File parentDir) {
        for (var aPackage : packages) {
            var dir = new File(parentDir, aPackage.getName());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            forPackages(aPackage.getSubPackages(), dir);

        }

    }
}
