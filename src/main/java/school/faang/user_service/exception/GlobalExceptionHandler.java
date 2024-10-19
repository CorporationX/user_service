package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.event.exceptions.DataValidationException;
import school.faang.user_service.exception.event.exceptions.InsufficientSkillsException;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;
import school.faang.user_service.exception.handler.ErrorResponse;
import school.faang.user_service.exception.mentorship_request.LittleTimeAfterLastRequestException;
import school.faang.user_service.exception.mentorship_request.RequestToHimselfException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestRejectException;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.exceptions.EventRegistrationException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            BadRequestException.class,
            DataValidationException.class,
            ValidationException.class,
            RecommendationRequestNotFoundException.class,
            EventRegistrationException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(RuntimeException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            UserNotFoundException.class,
            EntityNotFoundException.class,
            InvitationEntityNotFoundException.class
    })
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(RuntimeException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler({
            UserDeactivatedException.class,
            RecommendationRequestRejectException.class,
            UserAlreadyExistsException.class,
            InvitationCheckException.class,
            RequestToHimselfException.class
    })
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleConflictExceptions(RuntimeException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage(), ex);
        return ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""))
                );
    }

    @ExceptionHandler(LittleTimeAfterLastRequestException.class)
    @ResponseStatus(TOO_MANY_REQUESTS)
    public ErrorResponse handleTooManyRequestsExceptions(RuntimeException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler(InsufficientSkillsException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handleForbiddenExceptions(RuntimeException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler({
            AvatarFetchException.class,
            MinioUploadException.class,
            RuntimeException.class
    })
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorExceptions(RuntimeException ex) {
        return getResponse(ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return getResponse(ex);
    }

    private ErrorResponse getResponse(Exception ex) {
        log.error("Exception occurred: {}", ex.toString(), ex);
        return new ErrorResponse(ex.getMessage());
    }
}
