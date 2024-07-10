package school.faang.user_service.exception;

public record ErrorResponse(
        String error,
        String message
) {
}