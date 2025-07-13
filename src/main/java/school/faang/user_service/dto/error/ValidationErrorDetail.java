package school.faang.user_service.dto.error;

public record ValidationErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {
}
