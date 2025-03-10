package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String ERROR_USER_NOT_FOUND = "User with the given ID: %d was not found.";

    private final UserRepository userRepository;

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_USER_NOT_FOUND, id)));
    }
}
