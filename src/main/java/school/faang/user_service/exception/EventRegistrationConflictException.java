package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EventRegistrationConflictException extends RuntimeException{

    public EventRegistrationConflictException (String message){
        super(message);
    } //TODO проверить, почему такое узкое исключение
    // переписать на более общее
}
