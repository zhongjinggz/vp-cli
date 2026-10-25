package plugins.vpcli.domain.outputmodel.pumelementexporter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.myuml.myclassifier.MyAttribute;
import plugins.vpcli.domain.myuml.myclassifier.MyClass;
import plugins.vpcli.domain.myuml.myclassifier.MyOperation;
import plugins.vpcli.domain.myuml.mydiagram.mydiagramelement.ProperMyDiagramElement;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlClassExporter extends PumlBlockExporter {
    @Inject
    public PumlClassExporter() {
    }

    @Override
    String getBlockType() {
        return "class";
    }

    @Override
    void writeBlockBody(PumlDiagram result, ProperMyDiagramElement myDiagramElement) {
        if (!(myDiagramElement.getModelElement() instanceof MyClass)) {
            return;
        }
        MyClass myClass = (MyClass) myDiagramElement.getModelElement();
        for (MyAttribute attribute : myClass.getAttributes()) {
            result.writeTerminalShape(formatAttributeLine(attribute));
        }
        for (MyOperation operation : myClass.getOperations()) {
            result.writeTerminalShape(formatOperationLine(operation));
        }
    }

    // 属性行：<可见性符号> <{static}?> <名称> [: 类型] [= 初值]，各段按字段有无省略
    private String formatAttributeLine(MyAttribute attribute) {
        StringBuilder sb = new StringBuilder();
        appendVisibility(sb, attribute.getVisibility());
        if (attribute.isStatic()) {
            sb.append("{static} ");
        }
        if (attribute.getName() != null) {
            sb.append(attribute.getName());
        }
        if (attribute.getType() != null && !attribute.getType().isEmpty()) {
            sb.append(": ").append(attribute.getType());
        }
        if (attribute.getInitialValue() != null && !attribute.getInitialValue().isEmpty()) {
            sb.append(" = ").append(attribute.getInitialValue());
        }
        return sb.toString();
    }

    // 操作行：<可见性符号> <{abstract}?> <{static}?> <名称>(参数[, ...]) [: 返回类型]
    private String formatOperationLine(MyOperation operation) {
        StringBuilder sb = new StringBuilder();
        appendVisibility(sb, operation.getVisibility());
        if (operation.isAbstract()) {
            sb.append("{abstract} ");
        }
        if (operation.isStatic()) {
            sb.append("{static} ");
        }
        if (operation.getName() != null) {
            sb.append(operation.getName());
        }
        sb.append('(');
        var parameters = operation.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            MyOperation.Parameter param = parameters.get(i);
            sb.append(formatParameter(param));
        }
        sb.append(')');
        if (operation.getReturnType() != null && !operation.getReturnType().isEmpty()) {
            sb.append(": ").append(operation.getReturnType());
        }
        return sb.toString();
    }

    private String formatParameter(MyOperation.Parameter param) {
        StringBuilder sb = new StringBuilder();
        if (param.getName() != null) {
            sb.append(param.getName());
        }
        if (param.getType() != null && !param.getType().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(": ");
            }
            sb.append(param.getType());
        }
        if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
            sb.append(" = ").append(param.getDefaultValue());
        }
        return sb.toString();
    }

    // 可见性映射：#14 —— public→+、private→-、protected→#、package→~，其余（含空/未知）→空
    private void appendVisibility(StringBuilder sb, String visibility) {
        String key = visibility == null ? "" : visibility;
        String symbol;
        switch (key) {
            case "public": symbol = "+"; break;
            case "private": symbol = "-"; break;
            case "protected": symbol = "#"; break;
            case "package": symbol = "~"; break;
            default: symbol = ""; break;
        }
        if (!symbol.isEmpty()) {
            sb.append(symbol).append(' ');
        }
    }
}
