package school.faang.user_service.dto;

public record ErrorResponse(
    int code,
    String message
) {
}
