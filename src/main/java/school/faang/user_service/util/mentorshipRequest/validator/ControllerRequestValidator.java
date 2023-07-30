package school.faang.user_service.util.mentorshipRequest.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.util.mentorshipRequest.exception.IncorrectIdException;

@Component
public class ControllerRequestValidator {

    public void validateRequest(long id) {
        if (id < 1) {
            throw new IncorrectIdException();
        }
    }
}
