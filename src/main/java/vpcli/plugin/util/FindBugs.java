package vpcli.plugin.util;

import java.util.Objects;

public class FindBugs {
    private FindBugs() {}

    public static void requireNonNull(Object object, String message) {

        if (object == null ) {
            throw new BugException(message);
        }
    }
}
