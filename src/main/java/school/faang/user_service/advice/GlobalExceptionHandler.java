package school.faang.user_service.advice;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.DtoDeactiv;
import school.faang.user_service.exception.DeactivationException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DeactivationException.class)
    public ResponseEntity<DtoDeactiv> deactivatingError(DeactivationException e) {
        return new ResponseEntity<>(new DtoDeactiv(e.getMessage(), e.getId()), HttpStatusCode.valueOf(500));
    }
}
