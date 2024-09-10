package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorMessageDto {
    private String message;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm: ss")
    private LocalDateTime timestamp;

    public ErrorMessageDto(String message) {
        this.message = message;
    }
}