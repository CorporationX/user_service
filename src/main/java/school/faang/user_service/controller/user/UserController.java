package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserIdValidator;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private  final UserIdValidator userIdValidator;

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        userIdValidator.validateId(userId);
        User user = userService.getUser(userId);
        return userMapper.toDto(user);
    }

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        List<User> users = userService.getUsers(ids);
        return userMapper.toDtoList(users);
    }
}
