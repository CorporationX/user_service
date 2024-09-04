package school.faang.user_service.exception.event.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InsufficientSkillsException extends RuntimeException {

    public InsufficientSkillsException(String message) {
        super(message);
    }
}
