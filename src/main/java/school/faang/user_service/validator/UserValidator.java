package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public User checkIfUserIsExist(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataValidationException("Такого юзера нет в БД");
        } else {
            return userRepository.findById(userId).get();
        }
    }
}

