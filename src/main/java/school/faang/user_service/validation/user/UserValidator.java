package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PasswordValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

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
        String numbers = "1234567890";
        String symbols = "!@#$%^&*()_+<,>./,\"']}[{;:№";
        if (userDto.getPassword().length() <= 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long");
        }
        if (!userDto.getPassword().contains(numbers)) {
            throw new PasswordValidationException("Password must contain at least 1 digit");
        }
        if (!userDto.getPassword().contains(symbols)) {
            throw new PasswordValidationException(String.format
                    ("Password must contain at least 1 special symbol \"%s\"", symbols));
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
