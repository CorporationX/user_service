package school.faang.user_service.service.exceptions;

import school.faang.user_service.service.exceptions.messageerror.MessageError;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(MessageError error){
        super(error.getMessage());

    }
}
