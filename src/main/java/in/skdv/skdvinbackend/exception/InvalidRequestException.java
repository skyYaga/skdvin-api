package in.skdv.skdvinbackend.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public InvalidRequestException(ErrorMessage errorMessage) {
        super(errorMessage.toString());
        this.errorMessage = errorMessage;
    }
}
