package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PasswordValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUserDtoFields(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isBlank()) {
            throw new DataValidationException("Username can't be empty");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new DataValidationException("E-mail can't be empty");
        }
        if (userDto.getPhone() == null || userDto.getPhone().isBlank()) {
            throw new DataValidationException("Phone can't be empty");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new DataValidationException("Password can't be empty");
        }
    }

    public void validatePassword(UserDto userDto) {
        String userPassword = userDto.getPassword();

        if (userPassword.length() <= 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long");
        }
        if (!userPassword.matches(".*[A-Z].*")) {
            throw new PasswordValidationException("Password must contain at least 1 uppercase letter");
        }
        if (!userPassword.matches(".*\\d.*")) {
            throw new PasswordValidationException("Password must contain at least 1 digit");
        }
        if (!userPassword.matches(".*[!@#$%^&*()_+<,>./\"'}{;:№].*")) {
            throw new PasswordValidationException("Password must contain at least 1 special symbol");
        }
    }

    public void validateIfUserExistsById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id %d doesn't exist", userId));
        }
    }

    public void validateIfUserIsActive(long userId) {
        validateIfUserExistsById(userId);
        if (!userRepository.findById(userId).get().isActive()) {
            throw new DataValidationException(String.format("User with id %d is deactivated", userId));
        }
    }
}
