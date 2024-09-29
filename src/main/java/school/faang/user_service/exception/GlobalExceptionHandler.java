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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException ex) {
        log.error("BadRequestException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse userNotFoundHandle(UserNotFoundException ex) {
        log.error("UserNotFoundException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserDeactivatedException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleUserDeactivatedException(UserDeactivatedException ex) {
        log.error("UserDeactivatedException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage());
        return ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""))
                );
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.error("DataValidationException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(InsufficientSkillsException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handleInsufficientSkillsException(InsufficientSkillsException ex) {
        log.error("InsufficientSkillsException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex) {
        log.error("ValidationException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(RecommendationRequestNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleRecommendationRequestNotFoundException(RecommendationRequestNotFoundException ex) {
        log.error("RecommendationRequestNotFoundException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(RecommendationRequestRejectException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleRecommendationRequestRejectException(RecommendationRequestRejectException ex) {
        log.error("RecommendationRequestRejectException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AvatarFetchException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAvatarFetchException(AvatarFetchException ex) {
        log.error("AvatarFetchException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MinioUploadException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMinioUploadException(MinioUploadException ex) {
        log.error("MinioUploadException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage() + ex.getCause().getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("UserAlreadyExistsException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EventRegistrationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleEventRegistrationException(EventRegistrationException ex) {
        log.error("EventRegistrationException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(InvitationCheckException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleInvitationCheckException(InvitationCheckException ex) {
        log.error("InvitationCheckException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(InvitationEntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleInvitationEntityNotFoundException(InvitationEntityNotFoundException ex) {
        log.error("InvitationEntityNotFoundException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(RuntimeException ex) {
        log.error("RuntimeException occurred: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }
}
