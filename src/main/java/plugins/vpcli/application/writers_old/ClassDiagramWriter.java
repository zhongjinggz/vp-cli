package plugins.vpcli.application.writers_old;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import plugins.vpcli.domain.myuml_old.mydiagram_old.OldClassDiagram;
import plugins.vpcli.domain.myuml_old.myclass_old.MyClass_old;
import plugins.vpcli.domain.myuml_old.mynary_old.MyNary_old;
import plugins.vpcli.domain.myuml.mypackage.MyPackage;
import plugins.vpcli.domain.myuml.mycommon.MyRelationship;
import plugins.vpcli.drivenadapter.FileCreator;

public class ClassDiagramWriter extends PlantUMLWriter {

    private final List<MyPackage> packages;
    private final List<MyClass_old> classes;
    private final List<MyRelationship> relationships;
    private List<MyNary_old> naries;
    private final FileCreator fileCreator;
    private final OldClassDiagram myDiagram;

    public ClassDiagramWriter(
        OldClassDiagram myDiagram,
        FileCreator fileCreator) {

        super(myDiagram.getNotes());
        this.myDiagram = myDiagram;
        this.packages = myDiagram.getExportedPackages();
        this.classes = myDiagram.getExportedClasses();
        this.relationships = myDiagram.getRelationshipDatas();
        this.setNaries(myDiagram.getExportedNary());
        this.fileCreator = fileCreator;
    }

    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        for (MyPackage aPackage : packages) {
            if (!aPackage.isSubpackage())
                plantUMLContent.append(writePackage(aPackage, ""));
        }

        for (MyClass_old myClass : classes) {
            if (!myClass.isInPackage())
                plantUMLContent.append(writeClass(myClass, ""));
        }

        for (MyNary_old myNary : naries) {
            if (!myNary.isInPackage())
                plantUMLContent.append(writeNary(myNary, ""));
        }

        plantUMLContent.append(writeNotes());

        for (MyRelationship relationship : relationships) {
            plantUMLContent.append(writeRelationship(relationship));
        }

        plantUMLContent.append("@enduml");
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private String writeNary(MyNary_old myNary, String indent) {

        StringBuilder naryString = new StringBuilder();
        String alias = myNary.getAlias();
        String name = myNary.getName();

        naryString.append(indent).append("diamond ")
            .append("\"").append(name).append("\"").append(" as ").append(alias).append("\n");

        return naryString.toString();
    }


    private String writePackage(MyPackage myPackage, String indent) {
        StringBuilder packageString = new StringBuilder();
        String name = formatName(myPackage.getName());

        packageString.append(indent).append("namespace ").append(name).append(" {\n");

        for (MyPackage subPackage : myPackage.getSubPackages()) {
            packageString.append(writePackage(subPackage, indent + "\t"));

        }

        packageString.append(indent).append("}\n");
        return packageString.toString();

    }

    private String writeClass(MyClass_old myClass, String indent) {
        StringBuilder classString = new StringBuilder();
        String name = formatName(myClass.getName());
        String aliasDeclaration = formatAlias(myClass.getName()).equals(myClass.getName()) ? "" : (" as " + formatAlias(myClass.getName()));

        // equivalents mapping
        Map<String, String> keywordStereotypes = new HashMap<>();
        keywordStereotypes.put("interface", "interface");
        keywordStereotypes.put("enumeration", "enum");
        keywordStereotypes.put("enum", "enum");
        keywordStereotypes.put("metaclass", "metaclass");
        keywordStereotypes.put("struct", "struct");
        keywordStereotypes.put("entity", "entity");

        classString.append(indent);
        // classString.append(writeVisibility(myClass.getVisibility()));

        if (myClass.isAbstract()) classString.append("abstract ");
        if (myClass.getStereotypes().size() == 1 && !myClass.isAbstract()) {
            String stereotype = myClass.getStereotypes().get(0).toLowerCase();
            if (keywordStereotypes.containsKey(stereotype)) {
                classString.append(keywordStereotypes.get(stereotype)).append(" ").append(name).append(aliasDeclaration);
            } else {
                // if not puml keyword
                classString.append("class ").append(name).append(aliasDeclaration).append(" <<").append(stereotype).append(">>");
            }
        } else {
            // Default to "class" with any stereotypes listed
            classString.append("class ").append(name).append(aliasDeclaration);
            if (!myClass.getStereotypes().isEmpty()) {
                String stereotypesString = myClass.getStereotypes().stream()
                    .map(stereotype -> "<<" + stereotype + ">>")
                    .collect(Collectors.joining(", "));
                classString.append(" ").append(stereotypesString);
            }
        }

        classString.append(" {\n");

        // Attributes
        writeAttributesAndOperations(myClass.getAttributes(), myClass.getOperations(), indent, classString);

        classString.append(indent).append("}\n");
        return classString.toString();
    }


    private String writeRelationship(MyRelationship relationship) {

        return relationship.toExportFormat();
    }

    public void setNaries(List<MyNary_old> naries) {
        this.naries = naries;
    }

    public void write(File parentDir) throws IOException {

        File outputFile = createOutputFile(
            myDiagram.getDiagramUIModel().getName(),
            parentDir);

        writeToFile(outputFile);
    }

    public File createOutputFile(String title, File exportLocation) throws IOException {
        // 放行所有语言的字母和数字（中文文件名）；空格、符号及 Windows 保留字符仍转下划线
        String fileName = title.replaceAll("[^\\p{L}\\p{N}]", "_") +
            ".puml";
        File outputFile = this.fileCreator.under(exportLocation, fileName);
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
        }
        return outputFile;
    }
}
