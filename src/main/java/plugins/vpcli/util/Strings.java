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

    public static String doubleQuote(String str) {
        return "\"" + normalizeEmpty(str) + "\"";
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
        private final StringBuilder content;

        private StringPatcher() {
            this("");
        }

        private StringPatcher(String initStr) {
            if (Strings.isBlank(initStr)) {
                content = new StringBuilder();
            } else {
                content = new StringBuilder(initStr);
            }
        }

        public StringPatcher patch(String thePatch) {
            if (Strings.isNotBlank(thePatch)) {
                if (content.length() > 0) {
                    content.append(" ");
                }
                content.append(thePatch);
            }
            return this;
        }

        public StringPatcher patchBefore(String thePatch) {
            if (Strings.isNotBlank(thePatch)) {
                if (content.length() > 0) {
                    content.insert(0, " ").insert(0, thePatch);
                } else {
                    content.append(thePatch);
                }
            }
            return this;
        }

        public boolean isBlank() {
            return content.length() == 0;
        }

        public boolean isNotBlank() {
            return !isBlank();
        }

        @Override
        public String toString() {
            return content.toString();
        }
    }
}
