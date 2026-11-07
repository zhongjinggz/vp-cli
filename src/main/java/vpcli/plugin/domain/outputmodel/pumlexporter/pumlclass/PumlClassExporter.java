package vpcli.plugin.domain.outputmodel.pumlexporter.pumlclass;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IEnumerationLiteral;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.ITemplateParameter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import vpcli.plugin.domain.outputmodel.pumldiagram.PumlDiagram;
import vpcli.plugin.domain.outputmodel.pumlexporter.PumlBlockExporter;
import vpcli.plugin.util.BugException;
import vpcli.plugin.util.Strings;

import static vpcli.plugin.util.Strings.isNotBlank;

@Singleton
public class PumlClassExporter extends PumlBlockExporter {
    @Inject
    public PumlClassExporter() {
    }

    @Override
    public String getShapeType() {
        return IShapeTypeConstants.SHAPE_TYPE_CLASS;
    }

    /**
     * 带 <<interface>> 的 class 为 interface
     * 带 <<enumeration>> 的 class 为 enum
     * 抽象类在关键字前加 abstract。
     */
    @Override
    public String getBlockType(IModelElement vpElement) {
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
            return "class";
        } else {
            throw new BugException("should be 'IClass' but is:" + vpElement.getClass());
        }
    }

    private boolean hasStereotype(IClass clazz, String stereotypeName) {
        return getStereotypeNames(clazz).contains(stereotypeName);
    }

    // 泛型类追加模板参数后缀 `<T, U>`。
    @Override
    protected String templateParams(IModelElement vpElement) {
        if (!(vpElement instanceof IClass)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        IClass clazz = (IClass) vpElement;
        ITemplateParameter[] params = clazz.toTemplateParameterArray();
        if (params == null) {
            return "";
        }
        StringBuilder names = new StringBuilder();
        for (ITemplateParameter param : params) {
            String name = param.getName();
            if (name == null || name.isEmpty()) {
                continue;
            }
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(name);
        }
        if (names.length() > 0) {
            result.append('<').append(names).append('>');
        }
        return result.toString();
    }

    /**
     * 步 #20：分区渲染类体。依次读取属性区、操作区、枚举值区三个行列表；
     * 凡该区非空才写
     * 只有单区时无 `--`，两区及以上时以 `--` 分隔。
     */
    @Override
    public void exportBlockBody(PumlDiagram result, IDiagramElement vpDiagramElement) {
        var vpElement = getVpElement(vpDiagramElement);
        IClass clazz = (IClass) vpElement;

        boolean wroteAny = exportAttributes(result, clazz);
        wroteAny = exportOperations(result, clazz, wroteAny);
        exportEnumLiterals(result, clazz, wroteAny);
    }

    private static @NonNull IModelElement getVpElement(IDiagramElement vpDiagramElement) {
        var vpElement = vpDiagramElement.getModelElement();
        if (vpElement == null) {
            throw new BugException("vpElement should not be null");
        }
        if (!(vpElement instanceof IClass)) {
            throw new BugException("vpElement should be IClass, but is " + vpElement.getClass());
        }
        return vpElement;
    }

    private void exportEnumLiterals(PumlDiagram result, IClass clazz, boolean wroteAny) {
        boolean wroteSeparator = false;

        var literals = clazz.enumerationLiteralIterator();

        while (literals.hasNext()) {
            if (wroteAny && !wroteSeparator) {
                result.writeText("--");
                wroteSeparator = true;
            }
            IEnumerationLiteral literal = (IEnumerationLiteral) literals.next();
            if (isNotBlank(literal.getName())) {
                result.writeText(literal.getName());
            }
        }
    }

    private boolean exportOperations(PumlDiagram result, IClass clazz, boolean alreadyWroteAny) {
        boolean wroteAny = alreadyWroteAny;
        boolean wroteSeparator = false;

        var operationIterator = clazz.operationIterator();
        while (operationIterator.hasNext()) {
            if (alreadyWroteAny && !wroteSeparator) {
                result.writeText("--");
                wroteSeparator = true;
            }
            result.writeText(
                formatOperation((IOperation) operationIterator.next())
            );
            wroteAny = true;
        }

        return wroteAny;
    }

    private boolean exportAttributes(PumlDiagram result, IClass clazz) {
        boolean hasAttributes = false;

        var attributeIterator = clazz.attributeIterator();
        while (attributeIterator.hasNext()) {
            result.writeText(
                formatAttribute((IAttribute) attributeIterator.next())
            );
            hasAttributes = true;
        }

        return hasAttributes;
    }

    // 属性行：<可见性符号> <{static}?> <名称> [: 类型] [= 初值]，各段按字段有无省略
    private static String formatAttribute(IAttribute attribute) {
        StringBuilder result = new StringBuilder();

        appendAttributeVisibility(attribute, result);
        appendAttributeStatic(attribute, result);
        appendAttributeName(attribute, result);
        appendAttributeType(attribute, result);
        appendAttributeInitialValue(attribute, result);

        return result.toString();
    }

    private static void appendAttributeInitialValue(IAttribute attribute, StringBuilder result) {
        String initialValue = attribute.getInitialValueAsString();
        if (initialValue != null && !initialValue.isEmpty()) {
            result.append(" = ").append(initialValue);
        }
    }

    private static void appendAttributeType(IAttribute attribute, StringBuilder result) {
        String type = attribute.getTypeAsString();
        if (type != null && !type.isEmpty()) {
            result.append(": ").append(type);
        }
    }

    private static void appendAttributeVisibility(IAttribute attribute, StringBuilder result) {
        appendVisibility(result, attribute.getVisibility());
    }

    private static void appendAttributeName(IAttribute attribute, StringBuilder result) {
        if (attribute.getName() != null) {
            result.append(attribute.getName());
        }
    }

    private static void appendAttributeStatic(IAttribute attribute, StringBuilder result) {
        if (IAttribute.SCOPE_CLASSIFIER.equals(attribute.getScope())) {
            result.append("{static} ");
        }
    }

    // VP 版操作行，与 myuml 版格式逐段对齐
    private static String formatOperation(IOperation operation) {
        StringBuilder result = new StringBuilder();

        appendOperationVisibility(operation, result);
        appendOperationAbstract(operation, result);
        appendOperationStatic(operation, result);
        appendOperationName(operation, result);
        appendOperationParameters(operation, result);
        appendOperationReturnType(operation, result);

        return result.toString();
    }

    private static void appendOperationReturnType(IOperation operation, StringBuilder result) {
        String returnType = operation.getReturnTypeAsString();
        if (returnType != null && !returnType.isEmpty()) {
            result.append(": ").append(returnType);
        }
    }

    private static void appendOperationParameters(IOperation operation, StringBuilder result) {
        result.append('(');
        for (int i = 0; i < operation.parameterCount(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(formatVpParameter(operation.getParameterByIndex(i)));
        }
        result.append(')');
    }

    private static void appendOperationName(IOperation operation, StringBuilder result) {
        if (operation.getName() != null) {
            result.append(operation.getName());
        }
    }

    private static void appendOperationStatic(IOperation operation, StringBuilder result) {
        if (IOperation.SCOPE_CLASSIFIER.equals(operation.getScope())) {
            result.append("{static} ");
        }
    }

    private static void appendOperationAbstract(IOperation operation, StringBuilder result) {
        if (operation.isAbstract()) {
            result.append("{abstract} ");
        }
    }

    private static void appendOperationVisibility(IOperation operation, StringBuilder sb) {
        appendVisibility(sb, operation.getVisibility());
    }

    private static String formatVpParameter(IParameter param) {
        StringBuilder result = new StringBuilder();
        if (param.getName() != null) {
            result.append(param.getName());
        }
        String type = param.getTypeAsString();
        if (type != null && !type.isEmpty()) {
            if (result.length() > 0) {
                result.append(": ");
            }
            result.append(type);
        }
        String defaultValue = param.getDefaultValueAsString();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            result.append(" = ").append(defaultValue);
        }
        return result.toString();
    }

    // 可见性映射：#14 —— public→+、private→-、protected→#、package→~，其余（含空/未知）→空
    private static void appendVisibility(StringBuilder sb, String vpVisibility) {
        String pumlVisibility;
        switch (Strings.normalizeEmpty(vpVisibility)) {
            case "public":
                pumlVisibility = "+";
                break;
            case "private":
                pumlVisibility = "-";
                break;
            case "protected":
                pumlVisibility = "#";
                break;
            case "package":
                pumlVisibility = "~";
                break;
            default:
                pumlVisibility = "";
                break;
        }
        sb.append(pumlVisibility).append(' ');
    }

}
