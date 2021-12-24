package in.skdv.skdvinbackend.exception;

public class InvalidDeletionException extends InvalidRequestException {

    public InvalidDeletionException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }

}