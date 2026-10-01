package plugins.vpcli.drivenadapter;

import plugins.vpcli.domain.myuml.upackage.UPackage;

import java.util.List;

public class TreePrinter {
    public void printPackageTree(List<UPackage> packages) {
        for (UPackage aPackage : packages) {
            System.out.println(aPackage.getName());
            printSubPackages(aPackage, "");
        }
    }

    public void printSubPackages(UPackage aPackage, String prefix) {
        var subPackages = aPackage.getSubPackages();
        int i = 0;
        int size = subPackages.size();
        for (var subPackage : subPackages) {
            boolean isLast = (i == size - 1);
            String branch = isLast ? "└── " : "├── ";
            System.out.println(prefix + branch + subPackage.getName());
            String subPrefix = prefix + (isLast ? "    " : "│   ");
            printSubPackages(subPackage, subPrefix);
            i++;
        }
    }
}
