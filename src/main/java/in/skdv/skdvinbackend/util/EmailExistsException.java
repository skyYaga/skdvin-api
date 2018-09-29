package in.skdv.skdvinbackend.util;

public class EmailExistsException extends Exception {

    public EmailExistsException(final String message) {
        super(message);
    }

}