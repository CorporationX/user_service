package school.faang.user_service.dto.error;

public record ErrorResponseDto(
    String message,
    int status
) {
}
