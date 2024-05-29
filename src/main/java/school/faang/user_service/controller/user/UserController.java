package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.avatar.ProfilePicServiceImpl;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final ProfilePicServiceImpl profilePicService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Create user")
    @PostMapping("creature")
    public UserDto createUser(@ParameterObject @RequestBody @Valid UserDto userDto){
        return userService.createUser(userDto);
    }

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

    @Operation(summary = "Get users by ids")
    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
