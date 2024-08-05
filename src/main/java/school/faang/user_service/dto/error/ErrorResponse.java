package school.faang.user_service.dto.error;

public record ErrorResponse(
    int code,
    String message
) {
}
