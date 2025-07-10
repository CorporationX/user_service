package school.faang.user_service.dto.error;

public record ErrorResponse(
        String error,
        String message
) {
}