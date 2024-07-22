package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public User validateUserExistence(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            UserNotFoundException exception = new UserNotFoundException(userId);
            log.error(exception.getMessage());
            throw exception;
        }
        return userOptional.get();
    }

    public boolean checkAllFollowersExist(List<Long> followerIds) {
        try {
            userRepository.findAllById(followerIds);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
