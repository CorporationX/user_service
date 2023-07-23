package school.faang.user_service.util.goal.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.DataValidationException;

@ControllerAdvice
public class GoalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public String handleDataValidationException(DataValidationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        return ex.getClass() + " " + ex.getMessage();
    }
}
