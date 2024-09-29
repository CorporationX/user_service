package school.faang.user_service.validator.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserIdIsPositiveAndNotNull(Long userId) {
        if (userId == null) {
            throw new ValidationException("User id can't be null");
        }
        if (userId < 0) {
            throw new ValidationException("User id can't be less than 0");
        }
    }

    public void validateUserIsExisted(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " not exists"));
    }

    public void validateEmailNotExists(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new ValidationException("User with email " + email + " already exists");
                });
    }

    public void validatePhoneNotExists(String phone) {
        userRepository.findByPhone(phone)
                .ifPresent(user -> {
                    throw new ValidationException("User with phone " + phone + " already exists");
                });
    }

    public void validateNameNotExists(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new ValidationException("User with username " + username + " already exists");
                });
    }

    public void validateFirstUserIdAndSecondUserIdNotEquals(long firstUserId,
                                                            long secondUserId,
                                                            String exceptionMsg) {
        if (firstUserId == secondUserId) {
            throw new ValidationException(exceptionMsg);
        }
    }

    public void validateUserConstrains(User user) {
        validateEmailNotExists(user.getEmail());
        validatePhoneNotExists(user.getPhone());
        validateNameNotExists(user.getUsername());
    }
}
