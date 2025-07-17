package school.faang.user_service.controller.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.goal.GoalErrorResponseDto;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.service.GoalService;

import static school.faang.user_service.enums.ErrorCode.*;

@Slf4j
@ControllerAdvice(assignableTypes = {GoalController.class, GoalService.class})
public class GoalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<GoalResponse> handleException(BusinessException be) {
        return ResponseEntity.status(be.getStatus()).body(new GoalErrorResponseDto(be));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<GoalResponse> handleException(HttpMessageNotReadableException hmnre) {
        log.error(hmnre.getMessage(), hmnre);
        return handleException(new BusinessException(HttpStatus.BAD_REQUEST, JSON_STRUCT_ERROR));
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<GoalResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return handleException(new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR));
    }
}
