package school.faang.user_service.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class PayloadException {
    private final HttpStatus httpStatus;
    private final String message;
    private final ZonedDateTime timestamp;
}
