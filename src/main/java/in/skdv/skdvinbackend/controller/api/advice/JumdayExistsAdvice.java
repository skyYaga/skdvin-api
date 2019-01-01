package in.skdv.skdvinbackend.controller.api.advice;

import in.skdv.skdvinbackend.exception.JumpdayExistsException;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class JumdayExistsAdvice {

    @Autowired
    private MessageSource messageSource;

    @ResponseBody
    @ExceptionHandler(JumpdayExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<GenericResult> jumpdayExistsHandler(JumpdayExistsException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                .body(new GenericResult(false, messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale())));
    }
}
