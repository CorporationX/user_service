package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean existsById(long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("пользователь с id= {} не существует", userId);
            throw new NoSuchElementException(String.format("user with id: %d is not exists", userId));
        }
        return true;
    }
}