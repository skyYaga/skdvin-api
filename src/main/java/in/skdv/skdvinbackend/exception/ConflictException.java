package in.skdv.skdvinbackend.exception;

import lombok.Getter;

@Getter
public abstract class ConflictException extends RuntimeException {

    private final ErrorMessage errorMessage;

    protected ConflictException(ErrorMessage errorMessage) {
        super(errorMessage.toString());
        this.errorMessage = errorMessage;
    }
}
