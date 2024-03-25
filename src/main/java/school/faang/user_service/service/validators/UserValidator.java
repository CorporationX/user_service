package school.faang.user_service.service.validators;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.exceptions.messageerror.MessageError;

@Data
@Component
public class UserValidator {
    UserRepository userRepository;

    public void userExistenceInRepo(long userId){
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION);
        }
    }
}
