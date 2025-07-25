package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@RestController
public class UserController {
    private final UserService userService;

    public UserDto create(CreateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateString(userDto.password(), "password");
        validateNotNull(userDto.countryId(), "country");
        return userService.create(userDto);
    }

    @PutMapping("/me")
    public UserDto update(
            @RequestBody
            UpdateUserDto dto
    ) {
        return userService.updateMe(dto);
    }

    public UserDto getById(long userId) {
        return userService.getById(userId);
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
