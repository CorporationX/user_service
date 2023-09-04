package school.faang.user_service.validator.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;

@Component
public class UserValidator {
    public void validateUserDto(@Valid UserDto userDto) {
    }
}
