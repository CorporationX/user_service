package school.faang.user_service.controller.exeption;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.error.ErrorResponse;
import school.faang.user_service.dto.error.ValidationErrorDetail;
import school.faang.user_service.dto.error.ValidationErrorResponse;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse("Entity not found", ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation", ex);
        List<ValidationErrorDetail> details = ex.getConstraintViolations().stream()
                .map(this::mapToValidationErrorDetail)
                .toList();
        return new ValidationErrorResponse(
                "Constraint violation",
                "Validation failed for one or more fields.",
                details
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse("Forbidden", ex.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidation(DataValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse("Data validation exception", ex.getMessage());
    }

    private ValidationErrorDetail mapToValidationErrorDetail(ConstraintViolation<?> violation) {
        String fieldName = extractFieldName(violation.getPropertyPath());
        return new ValidationErrorDetail(
                fieldName,
                violation.getMessage(),
                violation.getInvalidValue()
        );
    }

    private String extractFieldName(Path propertyPath) {
        String fieldName = null;
        for (Path.Node node : propertyPath) {
            fieldName = node.getName();
        }
        return fieldName;
    }
}
