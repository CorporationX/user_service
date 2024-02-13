package school.faang.user_service.handlers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Alexander Bulgakov
 */

@Getter
@Setter
@ToString
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String url;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public ErrorResponse(int statusCode, String message) {
        this(statusCode);
        this.message = message;
    }
}
