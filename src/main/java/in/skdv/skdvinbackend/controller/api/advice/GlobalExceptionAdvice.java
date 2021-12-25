package in.skdv.skdvinbackend.controller.api.advice;

import in.skdv.skdvinbackend.exception.*;
import in.skdv.skdvinbackend.util.GenericResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    private final MessageSource messageSource;

    @ResponseBody
    @ExceptionHandler({InvalidRequestException.class, InvalidDeletionException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    GenericResult<Void> invalidRequestHandler(InvalidRequestException ex) {
        return new GenericResult<>(false, messageSource.getMessage(ex.getErrorMessage().toString(), null, LocaleContextHolder.getLocale()));
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    GenericResult<Void> notFoundHandler(NotFoundException ex) {
        return new GenericResult<>(false, messageSource.getMessage(ex.getErrorMessage().toString(), null, LocaleContextHolder.getLocale()));
    }

    @ResponseBody
    @ExceptionHandler(JumpdayInternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    GenericResult<Void> jumpdayInternalErrorHandler(JumpdayInternalException ex) {
        return new GenericResult<>(false, messageSource.getMessage(ex.getErrorMessage().toString(), null, LocaleContextHolder.getLocale()));
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    GenericResult<Void> validationErrorHandler(BindException ex) {
        return new GenericResult<>(false, ex.getAllErrors().toString());
    }

    @ResponseBody
    @ExceptionHandler({AlreadyConfirmedException.class, NoSlotsLeftException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    GenericResult<Void> alreadyConfirmedHandler(ConflictException ex) {
        return new GenericResult<>(false, messageSource.getMessage(ex.getErrorMessage().toString(), null, LocaleContextHolder.getLocale()));
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    GenericResult<Void> fallbackErrorHandler() {
        return new GenericResult<>(false, messageSource.getMessage(ErrorMessage.INTERNAL_SERVICE_EXCEPTION.toString(), null, LocaleContextHolder.getLocale()));
    }

}
