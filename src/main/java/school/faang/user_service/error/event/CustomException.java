package school.faang.user_service.error.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorResponse errorResponse;
}