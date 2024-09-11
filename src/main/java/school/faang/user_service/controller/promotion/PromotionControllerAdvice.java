package school.faang.user_service.controller.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.promotion.PromotionCheckException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;

@Slf4j
@ControllerAdvice
public class PromotionControllerAdvice {

    @ExceptionHandler(PromotionCheckException.class)
    public ResponseEntity<ProblemDetail> promotionCheckExceptionHandler(PromotionCheckException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(PromotionNotFoundException.class)
    public ResponseEntity<ProblemDetail> promotionNotFoundExceptionHandler(PromotionNotFoundException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }
}
