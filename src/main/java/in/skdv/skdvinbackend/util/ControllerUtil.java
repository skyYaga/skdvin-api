package in.skdv.skdvinbackend.util;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerUtil {

    private ControllerUtil() {
        throw new IllegalStateException("Utility class that should not be instantiated.");
    }

    public static ResponseEntity<GenericResult<Void>> parseAssignmentResult(GenericResult<Void> result, MessageSource messageSource) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true));
        }

        if (result.getMessage().equals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString()) ||
                result.getMessage().equals(ErrorMessage.SELFASSIGNMENT_READONLY.toString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResult<>(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

}
