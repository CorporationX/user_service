package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new DataValidationException("Такого пользователя нет"));
        return userOptional.get();
    }

}
