package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                            LocalDateTime timeStamp,
                            String url,
                            String error,
                            String message,
                            int status
) {
}
