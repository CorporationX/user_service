package school.faang.user_service.util.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.util.exception.DataValidationException;

@Component
public class UserControllerValidator {

    public void validateId(long id) {
        if (id < 1) {
            throw new DataValidationException("Id must be greater than 0");
        }
    }
}
