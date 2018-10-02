package in.skdv.skdvinbackend.exception;

public class EmailExistsException extends Exception {

    public EmailExistsException(final String message) {
        super(message);
    }

}