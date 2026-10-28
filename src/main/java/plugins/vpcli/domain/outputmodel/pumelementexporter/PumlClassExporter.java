package plugins.vpcli.domain.outputmodel.pumelementexporter;

import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IEnumerationLiteral;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 步 #18：接口关键字优先——带 <<interface>> 泛型的类声明为 interface（接口通常非抽象）；
     * 步 #20：带 <<enumeration>> 泛型的类声明为 enum（枚举本质是带该泛型的 Class）；
     * 步 #15：抽象类在关键字前加 abstract。
     */
    @Override
    String buildBlockType(IModelElement vpElement) {
        if (vpElement instanceof IClass) {
            IClass clazz = (IClass) vpElement;
            if (hasStereotype(clazz, "interface")) {
                return "interface";
            }
            if (hasStereotype(clazz, "enumeration")) {
                return "enum";
            }
            if (clazz.isAbstract()) {
                return "abstract class";
            }
        }
        return getBlockType();
    }

    /** 判断元素是否带指定泛型名（忽略大小写）。 */
    private boolean hasStereotype(IClass clazz, String stereotypeName) {
        var it = clazz.stereotypeModelIterator();
        while (it.hasNext()) {
            IModelElement stereo = (IModelElement) it.next();
            String name = stereo.getName();
            if (name != null && name.equalsIgnoreCase(stereotypeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 步 #20：分区渲染类体。依次读取属性区、操作区、枚举值区三个行列表；
     * 凡该区非空才写，凡需输出一个新的非空区且前面已写过区，则先在两区之间补一行 `--`。
     * 只有单区时无 `--`，两区及以上时以 `--` 分隔（用户确认的统一分区规则）。
     */
    @Override
    void writeBlockBody(PumlDiagram result, IModelElement vpElement) {
        if (!(vpElement instanceof IClass)) {
            return;
        }
        IClass clazz = (IClass) vpElement;

        List<String> attributeLines = collectAttributeLines(clazz);
        List<String> operationLines = collectOperationLines(clazz);
        List<String> enumerationLines = collectEnumerationLines(clazz);

        boolean wroteAny = false;
        if (!attributeLines.isEmpty()) {
            for (String line : attributeLines) {
                result.writeTerminalShape(line);
            }
            wroteAny = true;
        }
        if (!operationLines.isEmpty()) {
            if (wroteAny) {
                result.writeTerminalShape("--");
            }
            for (String line : operationLines) {
                result.writeTerminalShape(line);
            }
            wroteAny = true;
        }
        if (!enumerationLines.isEmpty()) {
            if (wroteAny) {
                result.writeTerminalShape("--");
            }
            for (String line : enumerationLines) {
                result.writeTerminalShape(line);
            }
            wroteAny = true;
        }
    }

    private List<String> collectAttributeLines(IClass clazz) {
        List<String> lines = new ArrayList<>();
        var attributeIterator = clazz.attributeIterator();
        while (attributeIterator.hasNext()) {
            lines.add(formatVpAttributeLine((IAttribute) attributeIterator.next()));
        }
        return lines;
    }

    private List<String> collectOperationLines(IClass clazz) {
        List<String> lines = new ArrayList<>();
        var operationIterator = clazz.operationIterator();
        while (operationIterator.hasNext()) {
            lines.add(formatVpOperationLine((IOperation) operationIterator.next()));
        }
        return lines;
    }

    /** 枚举项行 = 枚举项名，无可见性符（旧架构当作“空可见性属性行”追加）。 */
    private List<String> collectEnumerationLines(IClass clazz) {
        List<String> lines = new ArrayList<>();
        var literalIterator = clazz.enumerationLiteralIterator();
        while (literalIterator.hasNext()) {
            IEnumerationLiteral literal = (IEnumerationLiteral) literalIterator.next();
            if (literal.getName() != null && !literal.getName().isEmpty()) {
                lines.add(literal.getName());
            }
        }
        return lines;
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
