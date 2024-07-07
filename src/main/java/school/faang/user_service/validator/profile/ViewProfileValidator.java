package school.faang.user_service.validator.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.validator.user.UserValidator;

@Component
@RequiredArgsConstructor
public class ViewProfileValidator {
    private final UserValidator userValidator;

    public void validate(long authorId, long receiverId) {
        userValidator.validateUserExistence(authorId);
        userValidator.validateUserExistence(receiverId);
    }
}
