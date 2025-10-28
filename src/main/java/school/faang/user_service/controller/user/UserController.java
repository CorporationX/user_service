package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody CreateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateString(userDto.password(), "password");
        validateNotNull(userDto.countryId(), "country");
        return userService.create(userDto);
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UpdateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateNotNull(userDto.countryId(), "country");
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
