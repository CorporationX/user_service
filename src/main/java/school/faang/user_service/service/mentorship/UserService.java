package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User with id " + userId + " not found.");
            }
        }
    }
}
