package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.RequestValidator;

@Component
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto create(CreateUserDto userDto) {
        RequestValidator.validateStringNotEmpty(userDto.username(), "username");
        RequestValidator.validateStringNotEmpty(userDto.email(), "email");
        RequestValidator.validateStringNotEmpty(userDto.password(), "password");
        RequestValidator.validateNotNull(userDto.countryId(), "country");
        return userService.create(userDto);
    }

    public UserDto update(long userId, UpdateUserDto userDto) {
        RequestValidator.validateStringNotEmpty(userDto.username(), "username");
        RequestValidator.validateStringNotEmpty(userDto.email(), "email");
        RequestValidator.validateNotNull(userDto.countryId(), "country");
        return userService.update(userId, userDto);
    }

    public UserDto getById(long userId) {
        return userService.getById(userId);
    }
}
