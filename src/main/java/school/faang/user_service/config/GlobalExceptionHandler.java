package school.faang.user_service.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        ex.printStackTrace();  // Печатает в консоль
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: " + ex.getClass().getSimpleName() + " — " + ex.getMessage());
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<String> handleInvalidFormat(InvalidFormatException ex) {
        ex.printStackTrace();  // Показывает ошибку преобразования типов
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка преобразования JSON: " + ex.getValue() + " → " + ex.getTargetType().getSimpleName());
    }
}