package in.skdv.skdvinbackend.controller.api.advice;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.JumpdayExistsException;
import in.skdv.skdvinbackend.exception.JumpdayInternalException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class JumdayAdviceTest {

    @Mock
    private MessageSource messageSource;
    private JumdayAdvice jumdayAdvice;
    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        jumdayAdvice = new JumdayAdvice(messageSource);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void jumpdayExistsHandler() {
        JumpdayExistsException jumpdayExistsException = new JumpdayExistsException(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString());
        Mockito.when(messageSource.getMessage(Mockito.eq(jumpdayExistsException.getMessage()), Mockito.isNull(), Mockito.any()))
            .thenReturn(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString());

        ResponseEntity<GenericResult> responseEntity = jumdayAdvice.jumpdayExistsHandler(jumpdayExistsException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString(), responseEntity.getBody().getMessage());
    }

    @Test
    public void jumpdayInternalErrorHandler() {
        JumpdayInternalException jumpdayInternalException = new JumpdayInternalException(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString());
        Mockito.when(messageSource.getMessage(Mockito.eq(jumpdayInternalException.getMessage()), Mockito.isNull(), Mockito.any()))
                .thenReturn(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString());

        ResponseEntity<GenericResult> responseEntity = jumdayAdvice.jumpdayInternalErrorHandler(jumpdayInternalException);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString(), responseEntity.getBody().getMessage());
    }
}