package school.faang.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String url;
    private int statusCode;
    private String error;
    private final String message;
}
