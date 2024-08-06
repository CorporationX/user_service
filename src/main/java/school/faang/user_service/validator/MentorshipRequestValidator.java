package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final UserService userService;


    public void userExists(Long id) {
        if (!userService.existsById(id)) {
            throw new DataValidationException("User {} does not exist!" + id);
        }
    }

    public void isIdsEqual(Long firstId, Long secondId) {
        if (firstId.equals(secondId)) {
            throw new DataValidationException("You cannot send request to yourself!");
        }
    }
}
