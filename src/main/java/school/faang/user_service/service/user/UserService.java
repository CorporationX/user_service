package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    var message = "User with this id not found: " + id;
                    return new IllegalArgumentException(message);
                });
    }

    public List<User> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            var message = "Users with this ids not found";
            throw new IllegalArgumentException(message);
        }
        return users;
    }
}
