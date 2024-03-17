package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @Operation(summary = "Create new user")
    @PostMapping("/users/create")
    public UserDto create(@RequestBody UserDto userDto) {
        userValidator.validateUserDtoFields(userDto);
        return userService.create(userDto);
    }

    @Operation(summary = "Get premium users with filters")
    @PostMapping("/users/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        if (userId < 1) {
            throw new DataValidationException("User's id can't be less than 1");
        }
        return userService.getUser(userId);
    }

    @Operation(summary = "Get users by their ids")
    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> usersIds) {
        return userService.getUsersByIds(usersIds);
    }

    @Operation(summary = "Deactivate user by id")
    @PutMapping("/users/deactivate/{userId}")
    public UserDto deactivateUser(@PathVariable long userId) {
        return userService.deactivateUser(userId);
    }
}
