package in.skdv.skdvinbackend.exception;

public class NoSlotsLeftException extends ConflictException {

    public NoSlotsLeftException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
