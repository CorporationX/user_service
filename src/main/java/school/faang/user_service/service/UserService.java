package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(Long userId) {
        if (userId != null) {
            return userRepository.findById(userId).orElseThrow(() ->
                    new EntityNotFoundException("Invalid request. Requester user not found"));
        }
        throw new NullPointerException();
    }
}
