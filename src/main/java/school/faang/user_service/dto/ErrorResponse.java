package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    @JsonFormat(pattern = "HH:mm:ss dd-MM-yyyy")
    private LocalDateTime dateTime;
    private String exception;
    private String message;
    private int status;
}
