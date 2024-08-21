package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public boolean doAllUsersExist(List<Long> userIds) {
        List<User> allById = userRepository.findAllById(userIds);
        return allById.size() == userIds.size();
    }

    public void validateUserExistence(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("there is no User with id: " + userId);
        }
    }
}
