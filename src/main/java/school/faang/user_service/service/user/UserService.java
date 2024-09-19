package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.recomendation.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User getUser(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            log.error("User with {} id doesn't exist!", id);
            throw new DataValidationException("User doesn't exist!");
        }
    }
}
