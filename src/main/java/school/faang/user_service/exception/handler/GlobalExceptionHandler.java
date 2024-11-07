package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.exception.handler.ErrorResponse;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.exception.MaxActiveGoalsReachedException;
import school.faang.user_service.exception.MentorNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        return new ErrorResponse("Resource Not Found", ex.getMessage());
    }

    @ExceptionHandler(MaxActiveGoalsReachedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMaxActiveGoalsReached(MaxActiveGoalsReachedException ex) {
        log.error("Max active goals reached exception: {}", ex.getMessage());
        return new ErrorResponse("Max Active Goals Reached", ex.getMessage());
    }

    @ExceptionHandler(MentorNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMentorNotFound(MentorNotFoundException ex) {
        log.error("Mentor not found exception: {}", ex.getMessage());
        return new ErrorResponse("Mentor Not Found", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state exception: {}", ex.getMessage());
        return new ErrorResponse("Illegal State", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobalException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        return new ErrorResponse("Unexpected Error", "An unexpected error occurred. Please try again later.");
    }
}