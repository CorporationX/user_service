package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public boolean findUserByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean findUserByPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }
}
