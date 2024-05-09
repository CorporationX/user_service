package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователь с id " + userId + " не найден"));
    }
}
