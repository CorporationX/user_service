package school.faang.user_service.validator.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.s3.FileUploadException;
import school.faang.user_service.repository.UserRepository;

@Component
@AllArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    private final UserContext userContext;

    public boolean doesUserExistsById(long id) {
        return userRepository.existsById(id);
    }

    public void isCurrentUser(Long id) {
        if (!(userContext.getUserId() == id)) {
            throw new DataValidationException("The session belongs to another user");
        }
    }

    public void isValidUserAvatarId(String avatarId) {
        if (avatarId == null) {
            throw new FileUploadException("Avatar id can't be nullable");
        }
    }
}
