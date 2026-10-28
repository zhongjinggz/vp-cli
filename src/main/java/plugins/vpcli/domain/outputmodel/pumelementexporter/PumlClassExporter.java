package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

@Singleton
public class PumlClassExporter extends PumlBlockExporter {
    @Inject
    public PumlClassExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_CLASS;
    }

    @Override
    String getBlockType() {
        return "class";
    }

    /** 步 #15：抽象类在关键字前加 abstract。 */
    @Override
    String buildBlockType(IModelElement vpElement) {
        if (vpElement instanceof IClass && ((IClass) vpElement).isAbstract()) {
            return "abstract class";
        }
        return getBlockType();
    }

    /**
     * 步 A8：直接读 VP 原生 IClass 的成员（attributeIterator/operationIterator）渲染类体，
     * 可见性/静态/抽象/参数/初值映射沿用 #14/#12/#13 结论。
     */
    @Override
    void writeBlockBody(PumlDiagram result, IModelElement vpElement) {
        if (!(vpElement instanceof IClass)) {
            return;
        }
        IClass clazz = (IClass) vpElement;
        var attributeIterator = clazz.attributeIterator();
        while (attributeIterator.hasNext()) {
            result.writeTerminalShape(formatVpAttributeLine((IAttribute) attributeIterator.next()));
        }
        var operationIterator = clazz.operationIterator();
        while (operationIterator.hasNext()) {
            result.writeTerminalShape(formatVpOperationLine((IOperation) operationIterator.next()));
        }
    }

    // 属性行：<可见性符号> <{static}?> <名称> [: 类型] [= 初值]，各段按字段有无省略
    private String formatVpAttributeLine(IAttribute attribute) {
        StringBuilder sb = new StringBuilder();
        appendVisibility(sb, attribute.getVisibility());
        if (IAttribute.SCOPE_CLASSIFIER.equals(attribute.getScope())) {
            sb.append("{static} ");
        }
        if (attribute.getName() != null) {
            sb.append(attribute.getName());
        }
        String type = attribute.getTypeAsString();
        if (type != null && !type.isEmpty()) {
            sb.append(": ").append(type);
        }
        String initialValue = attribute.getInitialValueAsString();
        if (initialValue != null && !initialValue.isEmpty()) {
            sb.append(" = ").append(initialValue);
        }
        return sb.toString();
    }

    // VP 版操作行，与 myuml 版格式逐段对齐
    private String formatVpOperationLine(IOperation operation) {
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
        for (int i = 0; i < operation.parameterCount(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatVpParameter(operation.getParameterByIndex(i)));
        }
        sb.append(')');
        String returnType = operation.getReturnTypeAsString();
        if (returnType != null && !returnType.isEmpty()) {
            sb.append(": ").append(returnType);
        }
        return sb.toString();
    }

    private String formatVpParameter(IParameter param) {
        StringBuilder sb = new StringBuilder();
        if (param.getName() != null) {
            sb.append(param.getName());
        }
        String type = param.getTypeAsString();
        if (type != null && !type.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(": ");
            }
            sb.append(type);
        }
        String defaultValue = param.getDefaultValueAsString();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            sb.append(" = ").append(defaultValue);
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
