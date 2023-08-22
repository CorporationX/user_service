package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.invalidFieldException.InvalidIdException;

@Component
public class UserControllerValidator {

    public void validateId(long id) {
        if (id < 1) {
            throw new InvalidIdException("Id must be greater than 0");
        }
    }
}
