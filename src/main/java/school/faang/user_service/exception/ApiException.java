package school.faang.user_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

public class ApiException extends RuntimeException {
    @Getter
    private HttpStatusCode httpStatus;

    public ApiException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
