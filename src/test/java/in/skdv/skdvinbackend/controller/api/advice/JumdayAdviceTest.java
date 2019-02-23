package in.skdv.skdvinbackend.controller.api.advice;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.JumpdayExistsException;
import in.skdv.skdvinbackend.exception.JumpdayInternalException;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class JumdayAdviceTest {

    @Mock
    private MessageSource messageSource;

    private JumdayAdvice jumdayAdvice;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        jumdayAdvice = new JumdayAdvice(messageSource);
    }

    @Test
    public void jumpdayExistsHandler() {
        JumpdayExistsException jumpdayExistsException = new JumpdayExistsException(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString());
        Mockito.when(messageSource.getMessage(Mockito.eq(jumpdayExistsException.getMessage()), Mockito.isNull(), Mockito.any()))
            .thenReturn(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString());

        ResponseEntity<GenericResult> responseEntity = jumdayAdvice.jumpdayExistsHandler(jumpdayExistsException);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertFalse(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString(), responseEntity.getBody().getMessage());
    }

    @Test
    public void jumpdayInternalErrorHandler() {
        JumpdayInternalException jumpdayInternalException = new JumpdayInternalException(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString());
        Mockito.when(messageSource.getMessage(Mockito.eq(jumpdayInternalException.getMessage()), Mockito.isNull(), Mockito.any()))
                .thenReturn(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString());

        ResponseEntity<GenericResult> responseEntity = jumdayAdvice.jumpdayInternalErrorHandler(jumpdayInternalException);


        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assert.assertFalse(Objects.requireNonNull(responseEntity.getBody()).isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG.toString(), responseEntity.getBody().getMessage());
    }
}