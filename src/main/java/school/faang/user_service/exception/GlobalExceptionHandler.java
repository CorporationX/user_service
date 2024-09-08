package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecommendationRequestException.class)
    public ResponseEntity<Object> handleRecommendationRequestException(
            RecommendationRequestException exception
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return new ResponseEntity<>(body, exception.getHttpStatus());
    }
}
