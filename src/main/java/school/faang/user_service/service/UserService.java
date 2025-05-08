package school.faang.user_service.service;

import static school.faang.user_service.constants.ErrorMessages.getUserNotFoundError;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getUserNotFoundError(Long.toString(id))));
    }
}
