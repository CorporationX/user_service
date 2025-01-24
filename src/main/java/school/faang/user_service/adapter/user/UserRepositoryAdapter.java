package school.faang.user_service.adapter.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter {
    private final UserRepository userRepository;

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User с id: %s не найден!", userId)));
    }
}
