package in.skdv.skdvinbackend.util;

import lombok.Getter;

@Getter
public class GenericResult<E> {

    private final boolean success;
    private final String message;
    private final E payload;

    public GenericResult(boolean success) {
        this(success, null, null);
    }

    public GenericResult(boolean success, String message) {
        this(success, message, null);
    }

    public GenericResult(boolean success, E payload) {
        this(success, null, payload);
    }

    public GenericResult(boolean success, String message, E payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }
}
