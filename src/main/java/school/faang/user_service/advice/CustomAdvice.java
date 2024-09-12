package school.faang.user_service.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.annotation.CustomExceptionHandler;
import school.faang.user_service.dto.ResponseDto;
import school.faang.user_service.exception.DataValidationException;

@ControllerAdvice(annotations = CustomExceptionHandler.class)
public class CustomAdvice {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ResponseDto> handleException(DataValidationException e) {
        ResponseDto response = new ResponseDto(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
