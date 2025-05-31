package school.faang.user_service.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryAdapter {
    private final UserRepository userRepository;

    public User findById(long id) {
        log.info("Execution of the method User findById, parameters: id = {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id - %d not found", id)));
    }
}
