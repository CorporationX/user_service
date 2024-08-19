package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void uniqueUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }
    }

    public void uniqueEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new IllegalArgumentException("Email already exists!");
        }
    }

    public void uniquePhone(String phone) {
        Optional<User> user = userRepository.findByPhone(phone);
        if (user.isPresent()) {
            throw new IllegalArgumentException("Phone already exists!");
        }
    }

    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserValidationException("user id is either null or less than zero");
        }
    }

    public void validateThatUserIdExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserValidationException("user wasn't found");
        }
    }

    public Optional<User> findUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userOptional;
    }

    public boolean findUserByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean findUserByPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }
}
