package vpcli.plugin.util;

import org.jspecify.annotations.Nullable;
import org.jetbrains.annotations.Contract;
import java.util.Objects;

public class FindBugs {
    private FindBugs() {}


    @Contract("null -> fail")
    public static void requireNonNull(@Nullable Object object, String message) {

        if (object == null ) {
            throw new BugException(message);
        }
    }
}
