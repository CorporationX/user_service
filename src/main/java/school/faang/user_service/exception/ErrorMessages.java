package school.faang.user_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.EnumMap;
import java.util.Map;

@Getter
public enum ErrorMessages {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

    private final HttpStatus status;
    private final String message;

    ErrorMessages(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    private static final Map<HttpStatus, ErrorMessages> STATUS_TO_ERROR_MAP = new EnumMap<>(HttpStatus.class);

    static {
        for (ErrorMessages errorMessage : values()) {
            STATUS_TO_ERROR_MAP.put(errorMessage.getStatus(), errorMessage);
        }
    }

    public static ErrorMessages fromHttpStatus(HttpStatus status) {
        return STATUS_TO_ERROR_MAP.getOrDefault(status, INTERNAL_SERVER_ERROR);
    }
}