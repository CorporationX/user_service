package school.faang.user_service.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.annotation.AppExceptionHandler;
import school.faang.user_service.dto.ResponseDto;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@RestControllerAdvice(annotations = AppExceptionHandler.class)
public class CustomAdvice {
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException occurred: {}", e.getMessage(), e);
        return new ResponseDto(e.getMessage());
    }
}
