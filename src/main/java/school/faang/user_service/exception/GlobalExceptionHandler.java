package school.faang.user_service.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.exception.randomAvatar.AvatarGenerationException;
import school.faang.user_service.exception.s3.FileDownloadException;
import school.faang.user_service.exception.s3.FileUploadException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
        return new ResponseEntity<>(response(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AvatarGenerationException.class)
    public ResponseEntity<Object> handleAvatarGenerationException(AvatarGenerationException ex) {
        return new ResponseEntity<>(response(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Object> handleFileUploadException(FileUploadException ex) {
        return new ResponseEntity<>(response(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<Object> handleFileDownloadException(FileDownloadException ex) {
        return new ResponseEntity<>(response(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, String> response(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("time", LocalDateTime.now().toString());
        return response;
    }
}
