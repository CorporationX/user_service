package school.faang.user_service.validator.user;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.handler.exception.DataValidationException;

import static school.faang.user_service.validator.user.UserConstraints.*;

@Component
@NoArgsConstructor
public class UserValidator {
    public void validatePassword(UserDto userDto) {
        String userPassword = userDto.getPassword();

        if (userPassword.length() <= 8) {
            throw new DataValidationException(PASSWORD_SHORT.getMessage());
        }
        if (!userPassword.matches(".*[A-Z].*")) {
            throw new DataValidationException(PASSWORD_NOT_UPPERCASE.getMessage());
        }
        if (!userPassword.matches(".*\\d.*")) {
            throw new DataValidationException(PASSWORD_NOT_DIGITS.getMessage());
        }
        if (!userPassword.matches(".*[!@#$%^&*()_+<,>./\"'}{;:№].*")) {
            throw new DataValidationException(PASSWORD_NOT_SPECIAL_SYMBOL.getMessage());
        }
    }
}
