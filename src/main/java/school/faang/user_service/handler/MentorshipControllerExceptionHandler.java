package school.faang.user_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.exception.mentorship.NoUserMenteeException;
import school.faang.user_service.exception.mentorship.NoUserMentorException;
import school.faang.user_service.exception.mentorship.UserNotFoundException;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.message.mentorship.ExceptionResponseMessage;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(assignableTypes = MentorshipController.class)
public class MentorshipControllerExceptionHandler {

    @ExceptionHandler(InvalidIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseMessage handleInvalidIdException(InvalidIdException exception, WebRequest request) {
        registerException(HttpStatus.BAD_REQUEST, exception, request);
        return createExceptionResponseMessage(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseMessage handleUserNotFoundException(UserNotFoundException exception, WebRequest request) {
        registerException(HttpStatus.NOT_FOUND, exception, request);
        return createExceptionResponseMessage(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(NoUserMenteeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseMessage handleNoUserMenteeException(NoUserMenteeException exception, WebRequest request) {
        registerException(HttpStatus.NOT_FOUND, exception, request);
        return createExceptionResponseMessage(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(NoUserMentorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseMessage handleNoUserMentorException(NoUserMentorException exception, WebRequest request) {
        registerException(HttpStatus.NOT_FOUND, exception, request);
        return createExceptionResponseMessage(HttpStatus.NOT_FOUND, exception, request);
    }

    private ExceptionResponseMessage createExceptionResponseMessage(
            HttpStatus httpStatus,
            Exception exception,
            WebRequest request
    ) {
        return new ExceptionResponseMessage(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).split("=", 2)[1]
        );
    }

    private void registerException(
            HttpStatus httpStatus,
            Exception exception,
            WebRequest request
    ) {
        log.error(
                ExceptionMessage.REGISTER_EXCEPTION.getMessage(),
                request.getDescription(false).split("=", 2)[1],
                httpStatus.value(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }
}
