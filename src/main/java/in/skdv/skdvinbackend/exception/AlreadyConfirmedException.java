package in.skdv.skdvinbackend.exception;

public class AlreadyConfirmedException extends ConflictException {

    public AlreadyConfirmedException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }

}
