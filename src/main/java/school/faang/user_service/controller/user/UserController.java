package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.DeactivationService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final DeactivationService deactivationService;

    @Operation(summary = "Create new user")
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        userValidator.validateUserDtoFields(userDto);
        return userService.create(userDto);
    }

    @Operation(summary = "Get premium users with filters")
    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Min(value = 1, message = "User's id can't be less than 1") long userId) {
        return userService.getUser(userId);
    }

    @Operation(summary = "Get users by their ids")
    @PostMapping("/list")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> usersIds) {
        return userService.getUsersByIds(usersIds);
    }

    @Operation(summary = "Deactivate user by id")
    @PutMapping("/{userId}/deactivated")
    public UserDto deactivateUser(@PathVariable long userId) {
        return userService.deactivateUser(userId);
    }
}
