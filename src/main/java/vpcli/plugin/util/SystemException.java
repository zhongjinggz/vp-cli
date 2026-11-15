package vpcli.plugin.util;

public class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }
    public SystemException(Exception e) {
        super(e);
    }

}
