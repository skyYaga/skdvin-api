package in.skdv.skdvinbackend.exception;

public class JumpdayInternalException extends RuntimeException {

    public JumpdayInternalException(String errorCode) {
        super(errorCode);
    }
}
