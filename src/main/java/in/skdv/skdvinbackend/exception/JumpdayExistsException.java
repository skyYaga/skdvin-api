package in.skdv.skdvinbackend.exception;

public class JumpdayExistsException extends RuntimeException {

    public JumpdayExistsException(String errorCode) {
        super(errorCode);
    }
}
