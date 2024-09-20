package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileUploadException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(FileUploadException exception) {
        log.error("Error occurred: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong...");
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException exception) {
        log.error("Data error: {}", exception.getMessage());
        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }
}

