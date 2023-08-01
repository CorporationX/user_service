package school.faang.user_service.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.exception.GoalValidationException;
import school.faang.user_service.exception.RequestValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {

    @InjectMocks
    private CustomExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;


    @Test
    void handleRequestValidationException_ReturnsErrorResponse() {
        RequestValidationException exception = new RequestValidationException("Object cannot be null or empty");
        request = mock(HttpServletRequest.class);

        ResponseEntity<Object> response = exceptionHandler.handleRequestValidationException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();

        assertNotNull(errorResponse);
        assertEquals(request.getRequestURI(), errorResponse.getUrl());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid Request", errorResponse.getError());
        assertEquals(exception.getMessage(), errorResponse.getMessage());
    }

    @Test
    void testHandleGoalValidationException_ReturnsErrorResponse() {
        GoalValidationException exception = new GoalValidationException("Goal is not valid");
        request = mock(HttpServletRequest.class);

        ResponseEntity<Object> response = exceptionHandler.handleGoalValidationException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();

        assertNotNull(errorResponse);
        assertEquals(request.getRequestURI(), errorResponse.getUrl());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Goal Validation Error", errorResponse.getError());
        assertEquals(exception.getMessage(), errorResponse.getMessage());
    }
}