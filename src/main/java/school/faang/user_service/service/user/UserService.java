package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        if (userId == null) {
            logger.error("User ID is null");
            throw new IllegalArgumentException("User ID must not be null");
        }

        return userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found", userId);
            return new EntityNotFoundException("User with ID: " + userId + " not found");
        });
    }
}
