package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public void checkUserById(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with this ID is not found"));
    }
}
