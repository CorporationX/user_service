package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String url;

    private HttpStatus status;

    private String error;

    private String message;

    public ErrorResponse(String url, HttpStatus status, String error, String message) {
        timestamp = LocalDateTime.now();
        this.url = url;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
