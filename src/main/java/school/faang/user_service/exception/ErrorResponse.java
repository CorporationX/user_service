package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String url;

    private int status;

    private String error;

    private String message;

    public ErrorResponse(String url, int status, String error, String message) {
        timestamp = LocalDateTime.now();
        this.url = url;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
