package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findById(long id) {
        log.debug("Execution of the method findById, parameters: id = {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id - %d not found", id)));
    }
}
