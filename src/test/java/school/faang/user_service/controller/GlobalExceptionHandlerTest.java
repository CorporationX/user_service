package school.faang.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import school.faang.user_service.dto.ErrorResponseDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@WebAppConfiguration
@RequiredArgsConstructor
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    @Mock
    HttpServletRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(webRequest.getRequestURI()).thenReturn("/someUri");
    }

    @Test
    void handleMethodArgumentNotValidException() {
        Method method = new Object() {
        }.getClass().getEnclosingMethod();

        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        when(parameter.getExecutable()).thenReturn(method);

        FieldError error = Mockito.mock(FieldError.class);
        when(error.getField()).thenReturn("field");
        when(error.getDefaultMessage()).thenReturn("Field error message");

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter,
                bindingResult);

        Map<String, String> errorResponseMap = handler.handleMethodArgumentNotValidException(exception);

        assertAll(() -> {
            assertEquals(1, errorResponseMap.size());
            assertEquals("field", errorResponseMap.keySet().stream().findFirst().orElse(""));
            assertEquals("Field error message", errorResponseMap.get("field"));
        });
    }

    @Test
    void handleConstraintViolationException_shouldMatchAllFields() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getMessage()).thenReturn("Constraint violation exception");

        ErrorResponseDto response = handler.handleConstraintViolationException(exception, webRequest);

        assertAll(() -> {
            assertEquals("/someUri", response.getPath());
            assertEquals("Constraint violation exception", response.getError());
            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        });
    }

    @Test
    void handleDataValidationException() {
        DataValidationException exception = mock(DataValidationException.class);
        when(exception.getMessage()).thenReturn("Data validation exception");

        ErrorResponseDto response = handler.handleDataValidationException(exception, webRequest);

        assertAll(() -> {
            assertEquals("/someUri", response.getPath());
            assertEquals("Data validation exception", response.getError());
            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        });
    }

    @Test
    void handleEntityNotFoundException() {
        EntityNotFoundException exception = mock(EntityNotFoundException.class);
        when(exception.getMessage()).thenReturn("Entity not found exception");

        ErrorResponseDto response = handler.handleEntityNotFoundException(exception, webRequest);

        assertAll(() -> {
            assertEquals("/someUri", response.getPath());
            assertEquals("Entity not found exception", response.getError());
            assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        });
    }

    @Test
    void handleRuntimeException() {
        RuntimeException exception = mock(RuntimeException.class);
        when(exception.getMessage()).thenReturn("Internal server error");

        ErrorResponseDto response = handler.handleRuntimeException(exception, webRequest);

        assertAll(() -> {
            assertEquals("/someUri", response.getPath());
            assertEquals("Internal server error", response.getError());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        });
    }

    @Test
    void handleException() {
        Exception exception = mock(Exception.class);
        when(exception.getMessage()).thenReturn("Internal server error");

        ErrorResponseDto response = handler.handleException(exception, webRequest);

        assertAll(() -> {
            assertEquals("/someUri", response.getPath());
            assertEquals("Internal server error", response.getError());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        });
    }
}