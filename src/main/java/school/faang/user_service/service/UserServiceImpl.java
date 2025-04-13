package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserById(long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void banUser(long userId) {
        findUserById(userId).ifPresentOrElse(user -> {
            user.setBanned(true);
            userRepository.save(user);
            log.info("User: {} was banned", user.getUsername());
        }, () -> {
            String msg = "User with id " + userId + " not found";
            log.error(msg);
            throw new UserNotFoundException(msg);
        });
    }
}
