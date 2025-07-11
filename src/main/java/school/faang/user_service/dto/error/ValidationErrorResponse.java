package school.faang.user_service.dto.error;

import java.util.List;

public record ValidationErrorResponse(
        String error,
        String message,
        List<ValidationErrorDetail> details
) {
}
