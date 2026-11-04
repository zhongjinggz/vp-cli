package plugins.vpcli.util;

public class StringUtil {
    private StringUtil() {}

    public static boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }

    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }
}
