package school.faang.user_service.validation.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.repository.UserRepository;

@Component
@AllArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public boolean doesUserExistsById(long id) {
        return userRepository.existsById(id);
    }

}
