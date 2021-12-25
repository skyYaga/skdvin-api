package in.skdv.skdvinbackend.controller.api.advice;

import in.skdv.skdvinbackend.exception.*;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionAdviceTest {

    @Mock
    private MessageSource messageSource;
    private GlobalExceptionAdvice globalExceptionAdvice;
    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        globalExceptionAdvice = new GlobalExceptionAdvice(messageSource);
    }

    @AfterEach
    void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    void invalidRequestHandler() {
        InvalidRequestException invalidRequestException = new InvalidRequestException(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG);
        Mockito.when(messageSource.getMessage(Mockito.eq(invalidRequestException.getErrorMessage().toString()), Mockito.isNull(), Mockito.any()))
            .thenReturn(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString());

        GenericResult<Void> result = globalExceptionAdvice.invalidRequestHandler(invalidRequestException);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString(), result.getMessage());
    }

    @Test
    void notFoundHandler() {
        NotFoundException notFoundException = new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        Mockito.when(messageSource.getMessage(Mockito.eq(notFoundException.getErrorMessage().toString()), Mockito.isNull(), Mockito.any()))
            .thenReturn(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString());

        GenericResult<Void> result = globalExceptionAdvice.notFoundHandler(notFoundException);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    void jumpdayInternalErrorHandler() {
        JumpdayInternalException jumpdayInternalException = new JumpdayInternalException(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG);
        Mockito.when(messageSource.getMessage(Mockito.eq(jumpdayInternalException.getErrorMessage().toString()), Mockito.isNull(), Mockito.any()))
                .thenReturn(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString());

        GenericResult<Void> result = globalExceptionAdvice.jumpdayInternalErrorHandler(jumpdayInternalException);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString(), result.getMessage());
    }

    @Test
    void alreadyConfirmedHandler() {
        AlreadyConfirmedException alreadyConfirmedException = new AlreadyConfirmedException(ErrorMessage.APPOINTMENT_ALREADY_CONFIRMED);
        Mockito.when(messageSource.getMessage(Mockito.eq(alreadyConfirmedException.getErrorMessage().toString()), Mockito.isNull(), Mockito.any()))
                .thenReturn(ErrorMessage.APPOINTMENT_ALREADY_CONFIRMED.toString());

        GenericResult<Void> result = globalExceptionAdvice.alreadyConfirmedHandler(alreadyConfirmedException);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.APPOINTMENT_ALREADY_CONFIRMED.toString(), result.getMessage());
    }

    @Test
    void fallbackErrorHandler() {
        Mockito.when(messageSource.getMessage(Mockito.eq(ErrorMessage.INTERNAL_SERVICE_EXCEPTION.toString()), Mockito.isNull(), Mockito.any()))
                .thenReturn(ErrorMessage.INTERNAL_SERVICE_EXCEPTION.toString());

        GenericResult<Void> result = globalExceptionAdvice.fallbackErrorHandler();

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.INTERNAL_SERVICE_EXCEPTION.toString(), result.getMessage());
    }
}