package plugins.vpcli.drivenadapter;

import plugins.vpcli.domain.myuml.mypackage.MyPackage;

import java.util.List;

public class TreePrinter {
    public void print(List<MyPackage> packages) {
        for (MyPackage aPackage : packages) {
            System.out.println(aPackage.getName());
            printSubPackages(aPackage, "");
        }
    }

    public void printSubPackages(MyPackage aPackage, String prefix) {
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
