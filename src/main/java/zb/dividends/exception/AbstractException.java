package zb.dividends.exception;

public abstract class AbstractException extends RuntimeException {
    public abstract int getStatusCode();
    public abstract String getMessage();
}
