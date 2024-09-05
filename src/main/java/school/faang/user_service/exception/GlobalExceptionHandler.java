package school.faang.user_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({RestRuntimeException.class})
    public ResponseEntity<?> handleException(RestRuntimeException e) {
        return ResponseEntity.ok(
                Map.of(
                        "message",
                        e.getMessage()
                )
        );
    }
}
