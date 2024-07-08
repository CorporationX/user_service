package school.faang.user_service.exception.skill;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DataValidationException extends RuntimeException{

    public DataValidationException(String message) {
        super(message);
    }
}
