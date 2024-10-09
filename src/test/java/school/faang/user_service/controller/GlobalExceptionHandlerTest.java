package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorResponse;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private static final String TEXT = "Ошибка";
    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    @Mock
    private Constructor executable;

    @Test
    public void handleMethodArgumentNotValidTest() {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);


        FieldError fieldError = new FieldError("objectName", "fieldName", "Ошибка валидации");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        when(methodParameter.getExecutable()).thenReturn(executable);
        when(executable.toGenericString()).thenReturn(" ");

        ErrorResponse errorResponse = new ErrorResponse("fieldName", "Ошибка валидации");
        List<ErrorResponse> errorResponses = new ArrayList<>();
        errorResponses.add(errorResponse);

        List<ErrorResponse> result = globalExceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(errorResponses.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(errorResponses.get(0).getErrorDescription(), result.get(0).getErrorDescription());
    }

    @Test
    public void handleDataValidationTest() {
        DataValidationException dataValidationException = new DataValidationException(TEXT);

        ErrorResponse errorResponse = globalExceptionHandler.handleDataValidation(dataValidationException);

        assertEquals(TEXT, errorResponse.getErrorDescription());
    }

    @Test
    public void handleEntityNotFoundTest() {
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException(TEXT);

        ErrorResponse errorResponse = globalExceptionHandler.handleEntityNotFound(entityNotFoundException);

        assertEquals(TEXT, errorResponse.getErrorDescription());
    }

    @Test
    public void handleRuntimeTest() {
        RuntimeException runtimeException = new RuntimeException(TEXT);

        ErrorResponse errorResponse = globalExceptionHandler.handleRuntime(runtimeException);

        assertEquals(TEXT, errorResponse.getErrorDescription());
    }
}