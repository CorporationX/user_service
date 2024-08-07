package school.faang.user_service.validator.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@AllArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public boolean doesUserExistsById(long id) {
        return userRepository.existsById(id);
    }

}
