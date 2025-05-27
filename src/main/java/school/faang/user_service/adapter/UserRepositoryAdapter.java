package school.faang.user_service.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class UserRepositoryAdapter {

    private final UserRepository userRepository;

    public User getUserById (long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}
