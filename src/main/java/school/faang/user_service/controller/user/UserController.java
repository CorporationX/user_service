package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Get premium users")
    @PostMapping("premium")
    public List<UserDto> getPremiumUsers(@ParameterObject @RequestBody(required = false) UserFilterDto filter) {
        return userService.findPremiumUsers(filter);
    }

    @GetMapping("{userId}")
    @Operation(summary = "Get user by ID")
    public UserDto getUserById(@PathVariable long userId) {
        User user = userService.findUserById(userId);
        return userMapper.toDto(user);
    }

    @Operation(summary = "Deactivate user")
    @PostMapping("deactivation/{id}")
    public void deactivateUser(@Parameter @PathVariable Long id) {
        userService.deactivateUserById(id);
    }
}
