package plugins.plantUML.export.writers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;

public abstract class PlantUMLWriter {
    
    protected List<NoteData> notes = new ArrayList<NoteData>();
    
    public PlantUMLWriter(List<NoteData> notes) {
        if (notes != null) {
            this.notes = notes;
        }
    }

    public abstract void writeToFile(File file) throws IOException;
    
    protected String writeNotes() {
        StringBuilder notesContent = new StringBuilder();
        for (NoteData noteData : notes) {
            notesContent.append(writeNote(noteData)).append("\n");
        }
        return notesContent.toString();
    }
    
    protected String writeNote(NoteData noteData) {
        String content = noteData.getContent().replaceAll("\n", "\\\\n");
        
        // Return an empty string if content is null or empty
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        StringBuilder noteString = new StringBuilder();
        noteString.append("note ")
                  .append("\"").append(content).append("\" as ")
                  .append(noteData.getName());
        
        return noteString.toString();
    }

    protected String formatName(String name) {
        /*
         * \u53EA\u6709\u5B57\u6BCD\uFF08\u4EFB\u610F\u8BED\u8A00\uFF09\u548C\u6570\u5B57\u624D\u80FD\u76F4\u63A5\u4F5C\u4E3A PlantUML \u6807\u8BC6\u7B26\uFF0C
         * \u5176\u4F59\uFF08\u542B\u7A7A\u683C\uFF09\u7528\u5F15\u53F7\u5305\u88F9\u3002
         */
        if (!name.matches("[\\p{L}\\p{N}_]+")) { // \u653E\u884C\u6240\u6709\u8BED\u8A00\u7684\u5B57\u6BCD\u3001\u6570\u5B57\u548C\u4E0B\u5212\u7EBF
            return "\"" + name + "\"";
        }
        return name;
    }


    protected String formatAlias(String name) {
        // \u5B57\u6BCD\uFF08\u4EFB\u610F\u8BED\u8A00\uFF09\u548C\u6570\u5B57\u4FDD\u7559\uFF0C\u5176\u4F59\u5B57\u7B26\uFF08\u7A7A\u683C\u3001\u6807\u70B9\u3001\u7B26\u53F7\uFF09\u66FF\u6362\u4E3A\u4E0B\u5212\u7EBF
        return name.replaceAll("[^\\p{L}\\p{N}]", "_");
    }

    protected String writeVisibility(String visibility) {
        String visibilityCharacter = "";
        switch (visibility) {
            case "private":
                visibilityCharacter = "-";
                break;
            case "protected":
                visibilityCharacter = "#";
                break;
            case
                    "package": visibilityCharacter = "~";
                break;
            case "public":
                visibilityCharacter = "+";
                break;
        }
        return visibilityCharacter;
    }
    protected void writeAttributesAndOperations(List<AttributeData> attributes, List<OperationData> operations, String indent, StringBuilder classString) {
        for (AttributeData attribute : attributes) {
            String visibilityChar = writeVisibility(attribute.getVisibility());

            classString.append(indent).append("\t").append(visibilityChar).append(" ");
            if (attribute.isStatic()) classString.append("{static} ");
            classString.append(attribute.getName());

            if (attribute.getType() != null) {
                classString.append(": ").append(attribute.getType());
            }
            if (attribute.getInitialValue() != null) {
                classString.append(" = ").append(attribute.getInitialValue());
            }
            classString.append("\n");
        }

        // Add operations
        for (OperationData operation : operations) {
            String visibilityChar = writeVisibility(operation.getVisibility());

            classString.append(indent).append("\t").append(visibilityChar);

            if (operation.isAbstract()) classString.append("{abstract} ");

            if (operation.isStatic()) classString.append("{static} ");

            classString.append(operation.getName()).append("(");

            // Add parameters
            List<OperationData.Parameter> parameters = operation.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                OperationData.Parameter param = parameters.get(i);

                classString.append(param.getName());

                if (param.getType() != null && !param.getType().isEmpty()) {
                    classString.append(": ").append(param.getType());
                }

                if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
                    classString.append(" = ").append(param.getDefaultValue());
                }

                if (i < parameters.size() - 1) {
                    classString.append(", ");
                }
            }

            classString.append(")");

            if (operation.getReturnType() != null && !operation.getReturnType().isEmpty()) {
                classString.append(": ").append(operation.getReturnType());
            }

            classString.append("\n");
        }
    }
}
