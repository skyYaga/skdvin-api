package in.skdv.skdvinbackend.util;

import in.skdv.skdvinbackend.exception.ErrorMessage;

public class GenericResult<E> {

    private boolean success;
    private String message;
    private Exception exception;
    private E payload;

    public GenericResult(boolean success) {
        this(success, null, null, null);
    }

    public GenericResult(boolean success, String message) {
        this(success, message, null);
    }

    public GenericResult(boolean success, E payload) {
        this(success, null, null, payload);
    }

    public GenericResult(boolean success, ErrorMessage message) {
        this(success, message.toString(), null);
    }

    public GenericResult(boolean success, ErrorMessage message, Exception exception) {
        this(success, message.toString(), exception);
    }

    public GenericResult(boolean success, String message, Exception exception) {
        this(success, message, exception, null);
    }

    public GenericResult(boolean success, String message, Exception exception, E payload) {
        this.success = success;
        this.message = message;
        this.exception = exception;
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public E getPayload() {
        return payload;
    }

    public void setPayload(E payload) {
        this.payload = payload;
    }
}
