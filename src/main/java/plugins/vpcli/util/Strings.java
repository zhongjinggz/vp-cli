package plugins.vpcli.util;

public class Strings {
    private Strings() {
    }

    public static boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }

    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }

    public static String patch(String origin, String thePatch) {

        var result = origin == null ? "" : origin;

        return isBlank(thePatch) ? result : result + " " + thePatch;
    }

    public static StringPatcher buildStringPatcher(String initStr) {
        return new StringPatcher(initStr);
    }

    public static StringPatcher buildStringPatcher() {
        return new StringPatcher();
    }

    public static String normalizeEmpty(String str) {
        return str == null ? "" : str;
    }

    public static String normalizeNull(String str) {
        return str == null ? "null" : str;
    }


    public static class StringPatcher {
        private StringBuilder content;

        private StringPatcher() {
            content = new StringBuilder();
        }

        private StringPatcher(String initStr) {
            content = new StringBuilder(initStr);
        }

        public StringPatcher patch(String thePatch) {
            if (isNotBlank(thePatch)) {
                content.append(" ").append(thePatch);
            }
            return this;
        }

        @Override
        public String toString() {
            return content.toString();
        }
    }
}
