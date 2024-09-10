package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalStateException(
                "User with ID: %d does not exist.".formatted(userId)));
    }
}