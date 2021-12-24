package in.skdv.skdvinbackend.exception;

import lombok.Getter;

@Getter
public class JumpdayInternalException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public JumpdayInternalException(ErrorMessage errorMessage) {
        super(errorMessage.toString());
        this.errorMessage = errorMessage;
    }
}
